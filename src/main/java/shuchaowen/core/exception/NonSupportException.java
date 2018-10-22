package shuchaowen.core.exception;

public class NonSupportException extends RuntimeException{
	private static final long serialVersionUID = 5341163945147654715L;

	public NonSupportException(String message) {
		super(message);
	}
	
	public NonSupportException(Throwable e) {
		super(e);
	}
	
	public NonSupportException(Throwable e, String message) {
		super(message, e);
	}
}
