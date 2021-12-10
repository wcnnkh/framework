package io.basc.framework.redis.core;

public class TooManyClusterRedirectionsException extends RedisSystemException {

	private static final long serialVersionUID = -2818933672669154328L;

	/**
	 * Creates new {@link TooManyClusterRedirectionsException}.
	 *
	 * @param msg the detail message.
	 */
	public TooManyClusterRedirectionsException(String msg) {
		super(msg);
	}

	/**
	 * Creates new {@link TooManyClusterRedirectionsException}.
	 *
	 * @param msg   the detail message.
	 * @param cause the root cause from the data access API in use.
	 */
	public TooManyClusterRedirectionsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
