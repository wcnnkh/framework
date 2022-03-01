package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryValueOperations<K, V> extends ValueOperations<K, V> {
	boolean setIfAbsent(K key, V value, long exp, TimeUnit expUnit);

	boolean setIfPresent(K key, V value, long exp, TimeUnit expUnit);

	void set(K key, V value, long exp, TimeUnit expUnit);

	@Override
	default void set(K key, V value) {
		set(key, value, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	default boolean setIfAbsent(K key, V value) {
		return setIfAbsent(key, value, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	default boolean setIfPresent(K key, V value) {
		return setIfPresent(key, value, 0, TimeUnit.MILLISECONDS);
	}
}
