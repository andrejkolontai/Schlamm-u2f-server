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
	
	public KeyData(byte[] publicKey, byte[] keyHandle, String appId,int counter) {
		super();
		this.publicKey = publicKey;
		this.keyHandle = keyHandle;
		this.appId = appId;
		this.counter = counter;
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

	@Override
	public String toString() {
		return "KeyData [publicKey=" + Util.toHex(publicKey)
				+ ", keyHandle=" + Util.toHex(keyHandle) + ", appId="
				+ appId + ", counter=" + counter + "]";
	}
}
