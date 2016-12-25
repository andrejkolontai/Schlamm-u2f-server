package sz.schlamm.u2f.messages;

import java.io.Serializable;

import sz.schlamm.u2f.Util;

public class RegistrationResponseMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2594230188808783394L;
	byte[] registrationData;
	byte[] clientData;
	String version;

	public RegistrationResponseMessage() {
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	public String getRegistrationData() {
		return Util.toB64(this.registrationData);
	}

	public void setRegistrationData(String registrationData) {
		this.registrationData = Util.fromB64(registrationData);
	}

	public String getClientData() {
		return Util.toB64(this.clientData);
	}
	
	public byte[] getClientDataBytes() {
		return this.clientData;
	}
	
	public byte[] getRegistrationDataBytes() {
		return this.registrationData;
	}


	public void setClientData(String clientData) {
		this.clientData = Util.fromB64(clientData);
	}

	@Override
	public String toString() {
		return "RegistrationResponseMessage [getRegistrationData()="
				+ getRegistrationData() + ", getClientData()="
				+ getClientData() + "]";
	}
}