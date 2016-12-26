package sz.schlamm.u2f.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import sz.schlamm.u2f.Util;


public class SignatureData implements Serializable{
	
	private static final long serialVersionUID = -1330237482365158356L;
	
	
	private int userPresence;
	private int counter;
	private byte[] signature;
	
	private SignatureData(){}
	
	public int getUserPresence() {
		return userPresence;
	}
	public int getCounter() {
		return counter;
	}
	public byte[] getSignature() {
		return signature;
	}
	private void setUserPresence(int userPresence) {
		this.userPresence = userPresence;
	}
	private void setCounter(int counter) {
		this.counter = counter;
	}
	private void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString() {
		return "SignatureData [userPresence=" + userPresence + ", counter="
				+ counter + ", signature=" + Util.toB64(signature) + "]";
	}

	public static SignatureData fromBytes(byte[] data){
		ByteBuffer in = ByteBuffer.wrap(data);
		
		SignatureData ret = new SignatureData();
		
		ret.setUserPresence(in.get() & 0xff);
		ret.setCounter(in.getInt());
		
		byte[] sig = new byte[in.remaining()];
		in.get(sig);
		ret.setSignature(sig);
		
		return ret;
	}
}
