package run.soeasy.framework.retry;

public class RetryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RetryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RetryException(String msg) {
		super(msg);
	}

}
