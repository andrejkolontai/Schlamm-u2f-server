package sz.schlamm.u2f;

import java.io.Serializable;

import sz.schlamm.u2f.messages.RegistrationRequestMessage;
import sz.schlamm.u2f.messages.RegistrationResponseMessage;

public class RegistrationState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6755302628027322828L;

	private final RegistrationRequestMessage requestMessage;
	private RegistrationResponseMessage responseMessage;
	
	public RegistrationState(RegistrationRequestMessage requestMessage) {
		super();
		this.requestMessage = requestMessage;
	}

	public RegistrationResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(RegistrationResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public RegistrationRequestMessage getRequestMessage() {
		return requestMessage;
	}

	public String getChallenge(){
		return this.requestMessage.getChallenge();
	}
	
}
