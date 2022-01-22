package io.basc.framework.redis;

public enum ExpireOption {
	/**
	 * seconds -- Set the specified expire time, in seconds.
	 */
	EX,
	/**
	 * milliseconds -- Set the specified expire time, in milliseconds.
	 */
	PX,
	/**
	 * timestamp-seconds -- Set the specified Unix time at which the key will
	 * expire, in seconds
	 */
	EXAT,
	/**
	 * timestamp-milliseconds -- Set the specified Unix time at which the key will
	 * expire, in milliseconds.
	 */
	PXAT,
	/**
	 * Remove the time to live associated with the key.
	 */
	PERSIST
}