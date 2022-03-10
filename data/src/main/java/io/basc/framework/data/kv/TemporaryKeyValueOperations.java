package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryKeyValueOperations<K, V> extends TemporaryKeyOperations<K>, KeyValueOperations<K, V> {

	default V getAndTouch(K key, long exp, TimeUnit expUnit) {
		V value = get(key);
		if (value == null) {
			return null;
		}
		touch(key, exp, expUnit);
		return value;
	}

	boolean setIfAbsent(K key, V value, long exp, TimeUnit expUnit);

	boolean setIfPresent(K key, V value, long exp, TimeUnit expUnit);

	void set(K key, V value, long exp, TimeUnit expUnit);
}
