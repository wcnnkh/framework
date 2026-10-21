package run.soeasy.framework.web;

public class WebException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WebException(String message) {
		super(message);
	}

	public WebException(Throwable cause) {
		super(cause);
	}

	public WebException(String message, Throwable cause) {
		super(message, cause);
	}
}
