package scw.security.ip;

public class IPValidationFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IPValidationFailedException(String msg) {
		super(msg);
	}
}
