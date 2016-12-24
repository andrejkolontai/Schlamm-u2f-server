package sz.schlamm.u2f.messages;

import java.io.Serializable;

import sz.schlamm.u2f.Util;

public class RegisteredKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6938162558860965753L;
	private final String appId;
	private final String version;

	
	final byte[] keyHandle;
	
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