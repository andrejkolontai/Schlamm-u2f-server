package sz.schlamm.u2f;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import sz.schlamm.u2f.crypto.NISTP256KeyFactory;
import sz.schlamm.u2f.messages.RegistrationRequestMessage;
import sz.schlamm.u2f.messages.RegistrationResponseMessage;
import sz.schlamm.u2f.messages.SignRequestMessage;
import sz.schlamm.u2f.messages.SignResponseMessage;

public class Server implements Serializable{

	public static final String U2F_VERSION = "U2F_V2";

	
	static final Logger log = Logger.getLogger(Server.class.getName());
	
	private static final long serialVersionUID = 1355160473538073008L;
	
	private final Random rnd;
	private final String appId;
	
	private final Set<String> origins = new HashSet<String>();
	
	
	Server() {
		rnd = null;
		appId = null;
	}
	
	public Server(Random rnd, String appId) {
		this.rnd = rnd;
		this.appId = appId;
	}
	
	private static String normalizeOrigin(String origin) {
		try {
			URI originURI = new URI(origin);
			return originURI.getScheme()+"://"+originURI.getAuthority();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Wrong URI syntax "+origin);
		}
	}
	
	public Server addOrigin(String origin) {
		this.origins.add(normalizeOrigin(origin));
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
	
	public KeyData finishRegistration(RegistrationState registrationState) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException, CertificateException, U2FException {
		if (registrationState.getResponseMessage() == null)
			throw new IllegalStateException("no response");
		return processRegistrationResponseMessage(registrationState);
	}

	private KeyData processRegistrationResponseMessage(RegistrationState registrationState) throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException,U2FException {
		
		RegistrationRequestMessage request = registrationState.getRequestMessage();
		RegistrationResponseMessage response = registrationState.getResponseMessage();
		
		ByteArrayInputStream in = new ByteArrayInputStream(response.getRegistrationDataBytes());
		
		int reserved = in.read() & 0xff;
		if (reserved != 5) {
			throw new IllegalArgumentException("first byte should be 0x5");
		}
		
		byte[] publicKey = new byte[65];
		in.read(publicKey);
		
		int keyHandleLength = in.read()  & 0xff ;
		System.out.println("Key handle length: "+keyHandleLength);
		
		byte[] keyHandle = new byte[keyHandleLength];
		in.read(keyHandle);
		
		X509Certificate cert = X509Certificate.getInstance(in);
		
		System.out.println(cert.getSubjectDN());
		System.out.println(cert.getSigAlgName());
		System.out.println(cert);
		
		PublicKey key = cert.getPublicKey();
		System.out.println(key);
		
		byte[] signature = new byte[in.available()];
		in.read(signature);

		System.out.println("Sig length: "+signature.length);
		
		MessageDigest hasher = MessageDigest.getInstance("SHA-256");
		
		byte[] appHash = hasher.digest(this.appId.getBytes("UTF-8"));
		
		System.out.println("apphash len "+appHash.length);
		
		
		
		ByteBuffer sigSource = ByteBuffer.allocate(1 + 32 + 32 + keyHandleLength + 65).
				put((byte)0).
				put(appHash).
				put(hasher.digest(response.getClientDataBytes())).
				put(keyHandle).put(publicKey);
		
		
		Signature sig = Signature.getInstance("SHA256withECDSA");
		
		sig.initVerify(cert.getPublicKey());
		sig.update(sigSource.array());
		
		boolean result = sig.verify(signature);
		if (result) {
			return new KeyData(publicKey,keyHandle,this.appId,0);
		}else{
			throw new SignatureInvalidException();
		}
	}
	
	public SignRequestMessage createSignRequest(Collection<KeyData> userKeys) {
		byte[] challenge = new byte[32];
		rnd.nextBytes(challenge);
		return new SignRequestMessage(appId, challenge, userKeys);
	}
	
	public void processSignResponse(SignResponseMessage response,Collection<KeyData> userKeys) throws U2FException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		KeyData keyData = 
				userKeys.
				stream().
				filter(uk -> Arrays.equals(response.getKeyHandleBytes(), uk.getKeyHandle())).
				findAny().orElseThrow(U2FException::new);
		
		ByteBuffer sigIn = ByteBuffer.wrap(response.getSignatureDataBytes());
		
		int userPresence = sigIn.get() & 0xff;
		
		log.info("User presence: "+userPresence);
		
		int counter = sigIn.getInt();
		log.info("counter: "+counter);
		
		byte[] signature = new byte[sigIn.remaining()];
		sigIn.get(signature);
		
		MessageDigest hasher = MessageDigest.getInstance("SHA-256");
		
		byte[] sigSource = new byte[32 + 1 + 4 + 32];
		
		String clientDataStr = new String(response.getClientDataBytes());
		System.out.println("Client Data "+clientDataStr);
		
		ByteBuffer.wrap(sigSource).
				put(hasher.digest(this.appId.getBytes())).
				put((byte)userPresence).
				putInt(counter).
				put(hasher.digest(clientDataStr.getBytes()));
		
		Signature sig = Signature.getInstance("SHA256withECDSA");
	    
	    PublicKey bpub = NISTP256KeyFactory.decodeKey(keyData.getPublicKey());
		
		sig.initVerify(bpub);
		sig.update(sigSource);
		
		boolean result = sig.verify(signature);
		
		System.out.println(result);
		
	}
	
}
