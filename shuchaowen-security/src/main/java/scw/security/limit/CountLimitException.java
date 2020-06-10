package scw.security.limit;

public class CountLimitException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CountLimitException(String msg) {
		super(msg);
	}
}
