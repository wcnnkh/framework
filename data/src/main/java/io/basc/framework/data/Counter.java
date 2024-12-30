package io.basc.framework.data;

import io.basc.framework.data.kv.KeyOperations;

/**
 * 计数器
 * 
 * @author wcnnkh
 *
 */

public interface Counter extends KeyOperations<String> {
	public static interface CounterWrapper<W extends Counter> extends Counter, KeyOperationsWrapper<String, W> {

		@Override
		default long incr(String key, long delta, long initialValue) {
			return getSource().incr(key, delta, initialValue);
		}

		@Override
		default long incr(String key, long delta) {
			return getSource().incr(key, delta);
		}

		@Override
		default long decr(String key, long delta, long initialValue) {
			return getSource().decr(key, delta, initialValue);
		}

		@Override
		default long decr(String key, long delta) {
			return getSource().decr(key, delta);
		}
	}

	default long incr(String key, long delta) {
		return incr(key, delta, 0);
	}

	long incr(String key, long delta, long initialValue);

	default long decr(String key, long delta) {
		return decr(key, delta, 0);
	}

	long decr(String key, long delta, long initialValue);
}
