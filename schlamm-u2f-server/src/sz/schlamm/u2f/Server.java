package sz.schlamm.u2f;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import sz.schlamm.u2f.crypto.NISTP256KeyFactory;
import sz.schlamm.u2f.messages.ClientData;
import sz.schlamm.u2f.messages.RegistrationData;
import sz.schlamm.u2f.messages.RegistrationRequestMessage;
import sz.schlamm.u2f.messages.RegistrationResponseMessage;
import sz.schlamm.u2f.messages.SignRequestMessage;
import sz.schlamm.u2f.messages.SignResponseMessage;
import sz.schlamm.u2f.messages.SignatureData;

public class Server implements Serializable{

	public static final String U2F_VERSION = "U2F_V2";

	
	static final Logger log = Logger.getLogger(Server.class.getName());
	
	private static final long serialVersionUID = 1355160473538073008L;
	
	private final Random rnd;
	private final String appId;
	
	private final Set<String> origins = new HashSet<>();
	private final List<X509Certificate> attestationCAs = new ArrayList<>();
	
	
	Server() {
		rnd = null;
		appId = null;
	}
	
	public Server(Random rnd, String appId) {
		this.rnd = rnd;
		this.appId = appId;
	}
	
	public Server addOrigin(String origin) {
		this.origins.add(normalizeOrigin(origin));
		return this;
	}
	
	public Server addAttestationCA(X509Certificate ca) {
		this.attestationCAs.add(ca);
		return this;
	}
	
	
	private RegistrationRequestMessage createRegistrationRequest(Collection<KeyData> userKeys){
		byte[] challenge = new byte[32];
		rnd.nextBytes(challenge);
		return new RegistrationRequestMessage(this.appId, challenge,userKeys);
	}
	
	public RegistrationState startRegistration(Collection<KeyData> userKeys) {
		return new RegistrationState(createRegistrationRequest(userKeys));
	}
	
	public KeyData finishRegistration(RegistrationState registrationState) throws U2FException {
		if (registrationState.getResponseMessage() == null)
			throw new IllegalStateException("no response");
		return processRegistrationResponseMessage(registrationState);
	}
	
	private void validateClientData(String typ,ClientData clientData) throws U2FValidationException {
		log.info("validating clientData "+clientData);
		if (!typ.equals(clientData.getTyp())){
			throw new U2FValidationException("unexpected typ in clientData, expected "+typ+", got"+clientData.getTyp());
		}
		
		Set<String> allowedOrigins;
		
		if (this.origins.isEmpty()){
			log.warning("no valid origins specified, taking appId as origin");
			allowedOrigins = Collections.singleton(normalizeOrigin(appId));
		}else{
			allowedOrigins = this.origins;
		}
		log.info("validating origin, allowed: "+allowedOrigins);
		if (!allowedOrigins.contains(normalizeOrigin(clientData.getOrigin()))){
			throw new U2FValidationException("origin "+clientData.getOrigin()+" is none of the accepted origins");
		}
	}
	
	private void validateAttestation(X509Certificate attestationCert) throws U2FValidationException{
		if (this.attestationCAs.isEmpty()){
			log.warning("No attestation CAs configured, accepting any device");
			return;
		}
		log.info("validate attestation certificate, device "+attestationCert.getSubjectDN().toString());
		Principal issuer = attestationCert.getIssuerDN();
		log.info("attestation ca "+issuer.toString());
		X509Certificate attestationCA = this.attestationCAs.stream().
				filter(ca -> ca.getSubjectDN().equals(issuer)).
				findAny().orElseThrow(() -> new U2FValidationException(issuer.toString()+" is not known to me"));
		try {
			log.info("found attestation CA, verifying");
			attestationCert.verify(attestationCA.getPublicKey());
			log.info("attestation certificate validated");
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchProviderException 
				| CertificateException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new U2FValidationException("Signature invalid", e);
		}
	}

	private KeyData processRegistrationResponseMessage(RegistrationState registrationState) throws U2FException {
		
		try {
			log.info("processing registration response");
			RegistrationRequestMessage request = registrationState.getRequestMessage();
			RegistrationResponseMessage response = registrationState.getResponseMessage();
			ClientData clientData = ClientData.fromBytes(response.getClientDataBytes());
			log.info("clientData: "+clientData.toString());
			
			if (!clientData.getChallenge().equals(request.getChallenge())){
				log.log(Level.SEVERE,"Request challenge: "+request.getChallenge()+", Response challenge: "+clientData.getChallenge());
				throw new U2FValidationException("the request challenge does not match the response challenge");
			}
			
			validateClientData(ClientData.TYP_REGISTER, clientData);
			
			RegistrationData registrationData = RegistrationData.fromBytes(response.getRegistrationDataBytes());
			log.info("registrationData: "+registrationData.toString());
			
			validateAttestation(registrationData.getAttestationCert());
			String manufacturer = registrationData.getAttestationCert().getIssuerDN().toString();
			
			MessageDigest hasher = MessageDigest.getInstance("SHA-256");
			byte[] appHash = hasher.digest(this.appId.getBytes("UTF-8"));
			
			
			ByteBuffer sigSource = ByteBuffer.allocate(1 + 32 + 32 + registrationData.getKeyHandleLength() + 65).
					put((byte)0).
					put(appHash).
					put(hasher.digest(response.getClientDataBytes())).
					put(registrationData.getKeyHandle()).
					put(registrationData.getPublicKey());
			
			
			Signature sig = Signature.getInstance("SHA256withECDSA");
			
			sig.initVerify(registrationData.getAttestationCert().getPublicKey());
			sig.update(sigSource.array());
			
			boolean result = sig.verify(registrationData.getSignature());
			if (result) {
				return new KeyData(registrationData.getPublicKey(),registrationData.getKeyHandle(),this.appId,0,manufacturer);
			}else{
				throw new SignatureInvalidException();
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException	| UnsupportedEncodingException | SignatureException e) {
			throw new RuntimeException(e);
		}
	}
	
	public LoginState startLogin(Collection<KeyData> userKeys) {
		SignRequestMessage signRequestMessage = createSignRequest(userKeys);
		return new LoginState(signRequestMessage);
	}
	
	public KeyData finishLogin(LoginState loginState,Collection<KeyData> userKeys) throws U2FException {
		if (loginState.getSignResponseMessage() == null)
			throw new IllegalStateException("no response");
		return processSignResponse(loginState, userKeys);
	}
	
	private SignRequestMessage createSignRequest(Collection<KeyData> userKeys) {
		byte[] challenge = new byte[32];
		rnd.nextBytes(challenge);
		return new SignRequestMessage(appId, challenge, userKeys);
	}
	
	private KeyData processSignResponse(LoginState loginState,Collection<KeyData> userKeys) throws U2FException {
		
		log.info("processing sign response");
		try {
			SignResponseMessage response = loginState.getSignResponseMessage();
			SignRequestMessage request = loginState.getSignRequestMessage();
			
			ClientData clientData = ClientData.fromBytes(response.getClientDataBytes());
			log.info(clientData.toString());
			SignatureData signatureData = SignatureData.fromBytes(response.getSignatureDataBytes());
			log.info(signatureData.toString());
			
			if (!clientData.getChallenge().equals(request.getChallenge())){
				throw new U2FValidationException("the request challenge does not match the response challenge");
			}
			
			validateClientData(ClientData.TYP_AUTH, clientData);
			
			KeyData keyData = 
					userKeys.
					stream().
					filter(uk -> Arrays.equals(response.getKeyHandleBytes(), uk.getKeyHandle())).
					findAny().orElseThrow(() -> new U2FException("User key not found, keyhandle "+response.getKeyHandle()));
			
			int userPresence = signatureData.getUserPresence();
			if ((userPresence & 0x1) != 1) {
				throw new U2FValidationException("user presence was 0 (=user not present)");
			}
			
			int counter = signatureData.getCounter();
			if (keyData.getCounter() >= counter) {
				throw new U2FValidationException("device counter went backwards (cloned device?) our counter: "+keyData.getCounter()+", device counter "+counter);
			}
			keyData.setCounter(counter);
			
			MessageDigest hasher = MessageDigest.getInstance("SHA-256");
			
			byte[] sigSource = new byte[32 + 1 + 4 + 32];
			
			ByteBuffer.wrap(sigSource).
					put(hasher.digest(this.appId.getBytes())).
					put((byte)userPresence).
					putInt(counter).
					put(hasher.digest(response.getClientDataBytes()));
			
			Signature sig = Signature.getInstance("SHA256withECDSA");
			
			PublicKey bpub = NISTP256KeyFactory.decodeKey(keyData.getPublicKey());
			
			sig.initVerify(bpub);
			sig.update(sigSource);
			
			boolean result = sig.verify(signatureData.getSignature());
			if (!result){
				throw new U2FValidationException("signature invalid");
			}
			return keyData;
		} catch (InvalidKeyException | NoSuchAlgorithmException	| InvalidKeySpecException | SignatureException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String normalizeOrigin(String origin) {
		try {
			URI originURI = new URI(origin);
			return originURI.getScheme()+"://"+originURI.getAuthority();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Wrong URI syntax "+origin);
		}
	}	
}
