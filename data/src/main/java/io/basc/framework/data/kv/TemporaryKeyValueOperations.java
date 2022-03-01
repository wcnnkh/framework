package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryKeyValueOperations<K, V>
		extends TemporaryKeyOperations<K>, TemporaryValueOperations<K, V>, KeyValueOperations<K, V> {

	default V getAndTouch(K key, long exp, TimeUnit expUnit) {
		V value = get(key);
		if (value == null) {
			return null;
		}
		touch(key, exp, expUnit);
		return value;
	}
}
