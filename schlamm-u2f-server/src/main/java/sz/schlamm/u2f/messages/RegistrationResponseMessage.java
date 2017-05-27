package sz.schlamm.u2f.messages;

import java.io.Serializable;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

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

	public RegistrationResponseMessage(byte[] registrationData,	byte[] clientData, String version) {
		super();
		this.registrationData = registrationData;
		this.clientData = clientData;
		this.version = version;
	}
	
	public RegistrationResponseMessage(String registrationData,	String clientData, String version) {
		this(Util.fromB64(registrationData),Util.fromB64(clientData),version);
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
	
	public String toJSON(){
		return Json.createObjectBuilder().
				add("registrationData", this.getRegistrationData()).
				add("clientData", this.getClientData()).
				add("version", this.getVersion()).
				build().
				toString();
	}

	public static RegistrationResponseMessage fromJSON(String json){
		JsonObject parsed = Json.createReader(new StringReader(json)).readObject();
		return new RegistrationResponseMessage(
			parsed.getString("registrationData"), 
			parsed.getString("clientData"), 
			parsed.getString("version")
		);
	}
	
	@Override
	public String toString() {
		return "RegistrationResponseMessage [getRegistrationData()="
				+ getRegistrationData() + ", getClientData()="
				+ getClientData() + "]";
	}
}