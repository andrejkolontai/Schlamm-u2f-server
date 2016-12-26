package sz.schlamm.u2f.messages;

import java.io.Serializable;

import sz.schlamm.u2f.Util;

/**
 * The "RegisteredKey" object as specified by the FIDO. The client 
 * needs it to pass it to the token which in turn (re-)generates the
 * Public Key Pair out of it.
 * 
 * You dont's actually need to use it directly to communicate with the server
 * the "KeyData"-object is more suitable to store in user databases
 */
public class RegisteredKey implements Serializable {

	private static final long serialVersionUID = -6938162558860965753L;
	private final String appId;
	private final String version;

	
	final byte[] keyHandle;
	
	/**
	 * Yeah, the constructor
	 * 
	 * @param keyhandle the keyHandle 
	 * @param appId the appId
	 * @param version the version (right now, usually "U2F_V2")
	 */
	RegisteredKey(byte[] keyhandle,String appId,String version) {
		this.keyHandle = keyhandle;
		this.appId = appId;
		this.version = version;
	}

	public String getKeyHandle() {
		return Util.toB64(this.keyHandle);
	}
	
	public String getVersion(){
		return this.version;
	}
	
	public String getAppId(){
		return this.appId;
	}
}