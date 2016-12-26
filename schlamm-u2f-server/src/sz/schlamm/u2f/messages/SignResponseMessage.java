package sz.schlamm.u2f.messages;

import java.io.Serializable;

import sz.schlamm.u2f.Util;

public class SignResponseMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3041493429328373822L;

	private byte[] signatureData;
	private byte[] clientData;
	private byte[] keyHandle;
	
	
	public SignResponseMessage() {
		super();
	}

	public SignResponseMessage(byte[] signatureData, byte[] clientData,byte[] keyHandle) {
		super();
		this.signatureData = signatureData;
		this.clientData = clientData;
		this.keyHandle = keyHandle;
	}
	
	public SignResponseMessage(String signatureData, String clientData,String keyHandle) {
		this(Util.fromB64(signatureData),Util.fromB64(clientData),Util.fromB64(keyHandle));
	}
	
	public String getSignatureData() {
		return Util.toB64(this.signatureData);
	}
	public void setSignatureData(String signatureData) {
		this.signatureData = Util.fromB64(signatureData);
	}
	public String getClientData() {
		return Util.toB64(this.clientData);
	}
	public void setClientData(String clientData) {
		this.clientData = Util.fromB64(clientData);
	}
	public String getKeyHandle() {
		return Util.toB64(this.keyHandle);
	}
	public void setKeyHandle(String keyHandle) {
		this.keyHandle = Util.fromB64(keyHandle);
	}
	
	public byte[] getClientDataBytes() {
		return this.clientData;
	}
	
	public byte[] getKeyHandleBytes() {
		return this.keyHandle;
	}
	
	public byte[] getSignatureDataBytes() {
		return this.signatureData;
	}
}
