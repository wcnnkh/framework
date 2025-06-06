package run.soeasy.framework.dom;

public class DomException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DomException(String message) {
		super(message);
	}

	public DomException(Throwable e) {
		super(e);
	}

	public DomException(String message, Throwable e) {
		super(message, e);
	}
}
