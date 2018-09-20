package shuchaowen.core.exception;

public class KeyNonexistentException extends RuntimeException{
	private static final long serialVersionUID = 5341163945147654715L;

	public KeyNonexistentException(String message) {
		super(message);
	}
	
	public KeyNonexistentException(Throwable e) {
		super(e);
	}
	
	public KeyNonexistentException(Throwable e, String message) {
		super(message, e);
	}
}
