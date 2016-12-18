package sz.schlamm.u2f;

import java.io.Serializable;

public class RegistrationRequestMessage implements Serializable{
	
	private static final long serialVersionUID = -7925074341033284660L;

	
	final byte[] challenge;
	final String version;
	final String appId;
	
	public RegistrationRequestMessage(String appId,byte[] challenge) {
		if (challenge.length!=32)
			throw new IllegalArgumentException("Challenge must be 32 bytes long");
		this.appId = appId;
		this.challenge = challenge;
		this.version = Server.U2F_VERSION;
	}
	
	public String getChallenge() {
		return Util.toB64(this.challenge);
	}
	
	public String getVersion() {
		return version;
	}

	public String getAppId() {
		return appId;
	}
}