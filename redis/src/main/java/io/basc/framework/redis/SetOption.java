package io.basc.framework.redis;

public enum SetOption {
	/**
	 * Only set the key if it does not already exist.
	 */
	NX,
	/**
	 * Only set the key if it already exist.
	 */
	XX
}
