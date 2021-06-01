package scw.redis.core;

import scw.lang.NestedRuntimeException;

public class RedisInvalidSubscriptionException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>RedisInvalidSubscriptionException</code> instance.
	 *
	 * @param msg
	 * @param cause
	 */
	public RedisInvalidSubscriptionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Constructs a new <code>RedisInvalidSubscriptionException</code> instance.
	 *
	 * @param msg
	 */
	public RedisInvalidSubscriptionException(String msg) {
		super(msg);
	}
}
