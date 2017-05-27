package sz.schlamm.u2f;

public class U2FValidationException extends U2FException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1902097704910495980L;

	public U2FValidationException() {
		super();
	}

	public U2FValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public U2FValidationException(String arg0) {
		super(arg0);
	}

	public U2FValidationException(Throwable arg0) {
		super(arg0);
	}
}
