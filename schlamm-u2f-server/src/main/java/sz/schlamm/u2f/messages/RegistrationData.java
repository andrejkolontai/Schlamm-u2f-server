package sz.schlamm.u2f.messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import sz.schlamm.u2f.Util;

/*As specified in "FIDO U2F Raw Message Formats, 4.3"*/


public class RegistrationData implements Serializable{
	
	private static final long serialVersionUID = -2481423695726988231L;
	/*Skipped reserved byte*/
	byte[] publicKey;
	/*Skipped key handle length*/
	byte[] keyHandle;
	X509Certificate attestationCert;
	byte[] signature;
	
	
	
	private RegistrationData() {}
	
	public byte[] getPublicKey() {
		return publicKey;
	}
	public byte[] getKeyHandle() {
		return keyHandle;
	}
	public X509Certificate getAttestationCert() {
		return attestationCert;
	}
	public byte[] getSignature() {
		return signature;
	}
	private void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	private void setKeyHandle(byte[] keyHandle) {
		this.keyHandle = keyHandle;
	}
	private void setAttestationCert(X509Certificate attestationCert) {
		this.attestationCert = attestationCert;
	}
	private void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	public int getKeyHandleLength(){
		return this.keyHandle.length;
	}
	
	@Override
	public String toString	() {
		return "RegistrationData [publicKey=" + Util.toB64(publicKey)
				+ ", keyHandle=" + Util.toB64(keyHandle)
				+ ", attestationCert=" + attestationCert + ", signature="
				+ Util.toB64(signature) + "]";
	}

	public static RegistrationData fromBytes(byte[] data){
		try {
			RegistrationData ret = new RegistrationData();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			
			int reserved = in.read() & 0xff;
			if (reserved != 0x5){
				throw new IllegalArgumentException("the first byte should be 0x5, but was "+reserved);
			}
			
			byte[] publicKey = new byte[65];
			in.read(publicKey);
			ret.setPublicKey(publicKey);
			
			int keyHandleLength = in.read() & 0xff;
			byte[] keyHandle = new byte[keyHandleLength];
			in.read(keyHandle);
			ret.setKeyHandle(keyHandle);
			
			X509Certificate cert = X509Certificate.getInstance(in);
			ret.setAttestationCert(cert);
			
			byte[] signature = new byte[in.available()];
			in.read(signature);
			ret.setSignature(signature);
			
			return ret;
			
		} catch (IOException | CertificateException e) {
			throw new IllegalArgumentException("error parsing registration data",e);
		}
		
		
	}
	
}
