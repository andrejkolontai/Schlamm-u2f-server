package sz.schlamm.u2f.messages;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/*As specified in "FIDO U2F Raw Message Formats"*/

/**
 * This class represents the clientData from the FIDO specs.
 *  
 * The main benefit is the static fromBytes-Method that does all 
 * the parsing. 
 */

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

	/**
	 * The type of clientData, can be "navigator.id.getAssertion" (authentication) 
	 * or "navigator.id.finishEnrollment" (registration)
	 * @return the "typ"
	 */
	public String getTyp() {
		return typ;
	}
	private void setTyp(String typ) {
		this.typ = typ;
	}
	/**
	 * The challenge the client has signed. 
	 * @return the challenge as url-safe base64 encoded string without padding
	 */
	public String getChallenge() {
		return challenge;
	}
	private void setChallenge(String challenge) {
		this.challenge = challenge;
	}
	/**
	 * The origin, as seen by the client
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}
	private void setOrigin(String origin) {
		this.origin = origin;
	}
	/**
	 * Channel ID. As I am unable to test it yet I can't really say
	 * what this thing does.
	 * @return the cid
	 */
	public String getCid_pubkey() {
		return cid_pubkey;
	}
	private void setCid_pubkey(String cid_pubkey) {
		this.cid_pubkey = cid_pubkey;
	}

	/**
	 * In both the registration and authentication requests, the client sends
	 * it's "clientData" as a b64 encoded byte array. This function parses
	 * it and returns a ClientData object that we can use to access the 
	 * information 
	 * 
	 * @param data the clientData
	 * @return the parsed ClientData object
	 */
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
