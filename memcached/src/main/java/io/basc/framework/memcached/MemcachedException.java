package io.basc.framework.memcached;

import io.basc.framework.data.DataException;

public class MemcachedException extends DataException {
	private static final long serialVersionUID = 1L;

	public MemcachedException(String msg) {
		super(msg);
	}

	public MemcachedException(Throwable cause) {
		super(cause);
	}

	public MemcachedException(String message, Throwable cause) {
		super(message, cause);
	}
}
