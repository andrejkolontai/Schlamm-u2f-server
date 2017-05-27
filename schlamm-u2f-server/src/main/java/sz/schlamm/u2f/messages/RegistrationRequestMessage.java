package sz.schlamm.u2f.messages;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import sz.schlamm.u2f.KeyData;
import sz.schlamm.u2f.Server;
import sz.schlamm.u2f.Util;

public class RegistrationRequestMessage implements Serializable{
	
	private static final long serialVersionUID = -7925074341033284660L;

	
	private final byte[] challenge;
	private final String version;
	private final String appId;
	private final List<RegisteredKey> keys;
	
	public RegistrationRequestMessage(String appId,byte[] challenge,Collection<KeyData> userKeys) {
		if (challenge.length!=32)
			throw new IllegalArgumentException("Challenge must be 32 bytes long");
		this.appId = appId;
		this.challenge = challenge;
		this.version = Server.U2F_VERSION;
		this.keys = userKeys.stream().
				map(k -> new RegisteredKey(
						k.getKeyHandle(), 
						this.appId, 
						this.version)
				).collect(Collectors.toList());
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

	public List<RegisteredKey> getKeys() {
		return keys;
	}
	
	public String toJSON(){
		return Json.createObjectBuilder().
			add("challenge", this.getChallenge()).
			add("version", this.getVersion()).
			add("appId", this.getAppId()).
			add(
				/*The user might already have registered this device. So we need to
				 * send all the user's key handles to the client so it can check
				 * whether this device is already registered*/
				"keys",
				this.getKeys().stream().map(RegisteredKey::toJSON).
				collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
			).build().toString();
	}

	@Override
	public String toString() {
		return "RegistrationRequestMessage [challenge=" + Util.toB64(challenge) + ", version=" + version
				+ ", appId=" + appId + ", keys=" + keys + "]";
	}
}