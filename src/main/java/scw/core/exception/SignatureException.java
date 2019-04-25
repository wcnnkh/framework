package scw.core.exception;

public class SignatureException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public SignatureException(String message) {
		super(message);
	}
}