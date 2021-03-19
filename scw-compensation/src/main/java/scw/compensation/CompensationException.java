package scw.compensation;

import scw.lang.NestedRuntimeException;

public class CompensationException extends NestedRuntimeException{
	private static final long serialVersionUID = 1L;
	
	public CompensationException(String msg) {
		super(msg);
	}
	
	public CompensationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CompensationException(Throwable cause) {
		super(cause);
	}
}
