package shuchaowen.core.exception;

public class KeyAlreadyExistsException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public KeyAlreadyExistsException(String message) {
		super(message);
	}
}
