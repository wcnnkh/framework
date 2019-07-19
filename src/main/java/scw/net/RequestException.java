package scw.net;

public class RequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RequestException(String msg) {
		super(msg);
	}

	public RequestException(Throwable cause) {
		super(cause);
	}

	public RequestException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
