package sz.schlamm.u2f.messages;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/*As specified in "FIDO U2F Raw Message Formats"*/

public class ClientData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3889099419816483994L;
	
	public static final String TYP_AUTH = "navigator.id.getAssertion";
	public static final String TYP_REGISTER = "navigator.id.finishEnrollment";

	String typ;
	String challenge;
	String origin;
	String cid_pubkey;

	private ClientData(){}

	public String getTyp() {
		return typ;
	}
	private void setTyp(String typ) {
		this.typ = typ;
	}
	public String getChallenge() {
		return challenge;
	}
	private void setChallenge(String challenge) {
		this.challenge = challenge;
	}
	public String getOrigin() {
		return origin;
	}
	private void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getCid_pubkey() {
		return cid_pubkey;
	}
	private void setCid_pubkey(String cid_pubkey) {
		this.cid_pubkey = cid_pubkey;
	}
	
	public static ClientData fromBytes(byte[] data){
		ClientData ret = new ClientData();

		JsonReader reader = Json.createReader(new ByteArrayInputStream(data));
		
		JsonObject object = reader.readObject();
		if (object.containsKey("typ")){
			ret.setTyp(object.getString("typ"));
		}
		if (object.containsKey("origin")){
			ret.setOrigin(object.getString("origin"));
		}
		if (object.containsKey("challenge")){
			ret.setChallenge(object.getString("challenge"));
		}
		if (object.containsKey("cid_pubkey")){
			ret.setCid_pubkey(object.getString("cid_pubkey"));
		}
		
		return ret;
	}
	@Override
	public String toString() {
		return "ClientData [typ=" + typ + ", challenge=" + challenge
				+ ", origin=" + origin + ", cid_pubkey=" + cid_pubkey + "]";
	}
}
