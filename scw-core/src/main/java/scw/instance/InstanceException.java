package scw.instance;

public class InstanceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InstanceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
