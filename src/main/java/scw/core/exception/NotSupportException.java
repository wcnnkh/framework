package scw.core.exception;

public class NotSupportException extends RuntimeException{
	private static final long serialVersionUID = 5341163945147654715L;

	public NotSupportException(String message) {
		super(message);
	}
	
	public NotSupportException(Throwable e) {
		super(e);
	}
	
	public NotSupportException(Throwable e, String message) {
		super(message, e);
	}
}
