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

public class SignRequestMessage implements Serializable{

	private static final long serialVersionUID = -2375369369670304761L;
	
	final String appId;
	private final byte[] challenge;
	final String version;
	private final List<RegisteredKey> keys;
	
	
	public SignRequestMessage(String appId, byte[] challenge,Collection<KeyData> userKeys) {
		super();
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

	public String getAppId() {
		return appId;
	}

	public String getChallenge() {
		return Util.toB64(this.challenge);
	}

	public String getVersion() {
		return version;
	}

	public List<RegisteredKey> getKeys() {
		return keys;
	}
	
	public byte[] getChallengeBytes(){
		return this.challenge;
	}

	public String toJSON() {
		return Json.createObjectBuilder().
			add("appId", this.appId).
			add("challenge", Util.toB64(challenge)).
			add("version", this.version).
			add("keys",keys.stream().
					map(RegisteredKey::toJSON).
					collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
			).
			build().
			toString();
	}
}
