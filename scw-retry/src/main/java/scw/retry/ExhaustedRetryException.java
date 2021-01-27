package scw.retry;

public class ExhaustedRetryException extends RetryException {
	private static final long serialVersionUID = 1L;

	public ExhaustedRetryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ExhaustedRetryException(String msg) {
		super(msg);
	}
	
}
