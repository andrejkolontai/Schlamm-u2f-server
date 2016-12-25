package sz.schlamm.u2f;

import java.io.Serializable;

import sz.schlamm.u2f.messages.SignRequestMessage;
import sz.schlamm.u2f.messages.SignResponseMessage;

public class LoginState implements Serializable{
	
	private static final long serialVersionUID = 6337717629026675312L;
	
	
	private final SignRequestMessage signRequestMessage;
	private SignResponseMessage signResponseMessage;
	
	
	public LoginState(SignRequestMessage signRequestMessage) {
		super();
		this.signRequestMessage = signRequestMessage;
	}
	public SignResponseMessage getSignResponseMessage() {
		return signResponseMessage;
	}
	public void setSignResponseMessage(SignResponseMessage signResponseMessage) {
		this.signResponseMessage = signResponseMessage;
	}
	public SignRequestMessage getSignRequestMessage() {
		return signRequestMessage;
	}
	
	
}
