package sz.schlamm.u2f;

import java.io.Serializable;

public class KeyData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 351094531816729048L;
	private final byte[] publicKey;
	private final byte[] keyHandle;
	private final String appId;
	private int counter;
	private final String manufacturer;
	
	public KeyData(byte[] publicKey, byte[] keyHandle, String appId,int counter,String manufacturer) {
		super();
		this.publicKey = publicKey;
		this.keyHandle = keyHandle;
		this.appId = appId;
		this.counter = counter;
		this.manufacturer = manufacturer;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public byte[] getKeyHandle() {
		return keyHandle;
	}

	public String getAppId() {
		return appId;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	@Override
	public String toString() {
		return "KeyData [publicKey=" + Util.toB64(publicKey) + ", keyHandle=" + Util.toB64(keyHandle)
				+ ", appId=" + appId + ", counter=" + counter + ", manufacturer=" + manufacturer + "]";
	}
	
	
}
