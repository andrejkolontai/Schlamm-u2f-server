package sz.schlamm.u2f;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SignRequestMessage implements Serializable{

	private static final long serialVersionUID = -2375369369670304761L;
	
	private final String appId;
	private final byte[] challenge;
	private final String version;
	private final List<RegisteredKey> keys;
	
	
	public SignRequestMessage(String appId, byte[] challenge,Collection<KeyData> userKeys) {
		super();
		this.appId = appId;
		this.challenge = challenge;
		this.version = Server.U2F_VERSION;
		this.keys = userKeys.stream().map(RegisteredKey::new).collect(Collectors.toList());
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

	public class RegisteredKey implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		final byte[] keyHandle;
		
		private RegisteredKey(KeyData userKey) {
			this.keyHandle = Arrays.copyOf(userKey.getKeyHandle(), userKey.getKeyHandle().length);
		}

		public String getKeyHandle() {
			return Util.toB64(this.keyHandle);
		}

		public byte[] getKeyHandleBytes() {
			return this.keyHandle;
		}

		
		public String getVersion(){
			return SignRequestMessage.this.version;
		}
		
		public String getAppId(){
			return SignRequestMessage.this.appId;
		}
	}
}
