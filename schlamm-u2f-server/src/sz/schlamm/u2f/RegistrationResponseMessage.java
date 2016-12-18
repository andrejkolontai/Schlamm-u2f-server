package sz.schlamm.u2f;

import java.io.Serializable;

public class RegistrationResponseMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2594230188808783394L;
	byte[] registrationData;
	byte[] clientData;
	byte[] challenge;
	String version;
	String appId;

	public RegistrationResponseMessage() {
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getChallenge() {
		return Util.toB64(this.challenge);
	}

	public byte[] getChallengeBytes() {
		return challenge;
	}
	
	public void setChallenge(String challenge) {
		this.challenge = Util.fromB64(challenge);
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