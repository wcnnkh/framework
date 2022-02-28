package io.basc.framework.data;

/**
 * 计数器
 * 
 * @author wcnnkh
 *
 */

public interface Counter extends KeyOperations<String> {
	default long incr(String key, long delta) {
		return incr(key, delta, 0);
	}

	long incr(String key, long delta, long initialValue);

	default long decr(String key, long delta) {
		return decr(key, delta, 0);
	}

	long decr(String key, long delta, long initialValue);
}
