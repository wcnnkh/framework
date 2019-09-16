package scw.result.exception;

import scw.core.exception.NestedRuntimeException;

public class ResultException extends NestedRuntimeException{
	private static final long serialVersionUID = 1L;

	public ResultException(String msg) {
		super(msg);
	}

	public ResultException(Throwable cause) {
		super(cause);
	}
	
	public ResultException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
