package scw.integration.verification;

public class VerificationCodeExpiredException extends VerificationCodeException {
	private static final long serialVersionUID = 1L;

	public VerificationCodeExpiredException(String message) {
		super(message);
	}
}
