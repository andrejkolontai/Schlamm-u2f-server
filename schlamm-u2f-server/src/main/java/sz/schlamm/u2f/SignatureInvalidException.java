package sz.schlamm.u2f;

public class SignatureInvalidException extends U2FException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 469577313815868837L;

	public SignatureInvalidException() {
	}

	public SignatureInvalidException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SignatureInvalidException(String arg0) {
		super(arg0);
	}

	public SignatureInvalidException(Throwable arg0) {
		super(arg0);
	}

}
