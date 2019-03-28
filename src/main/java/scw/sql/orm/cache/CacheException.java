package scw.sql.orm.cache;

public class CacheException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CacheException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}
}
