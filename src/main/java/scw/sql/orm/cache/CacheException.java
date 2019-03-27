package scw.sql.orm.cache;

import scw.common.exception.NestedRuntimeException;

public class CacheException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public CacheException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}
}
