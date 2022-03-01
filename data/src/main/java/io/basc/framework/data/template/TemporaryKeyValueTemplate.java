package io.basc.framework.data.template;

import io.basc.framework.data.kv.KeyValueOperations;

public interface TemporaryKeyValueTemplate<K, V> extends TemporaryKeyTemplate<K>, KeyValueOperations<K, V> {

	default V getAndTouch(K key) {
		V value = get(key);
		if (value == null) {
			return value;
		}
		touch(key);
		return value;
	}
}
