package scw.result.exception;

public class ResultException extends RuntimeException{
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
