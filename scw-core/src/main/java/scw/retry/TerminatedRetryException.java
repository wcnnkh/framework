package scw.retry;

public class TerminatedRetryException extends RetryException {
	private static final long serialVersionUID = 1L;

	public TerminatedRetryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TerminatedRetryException(String msg) {
		super(msg);
	}

}
