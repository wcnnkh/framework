package io.basc.framework.redis;

import io.basc.framework.lang.NestedRuntimeException;

public class RedisInvalidSubscriptionException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public RedisInvalidSubscriptionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RedisInvalidSubscriptionException(String msg) {
		super(msg);
	}
}
