package io.basc.framework.data.kv;

import io.basc.framework.core.convert.TypeDescriptor;

public interface ObjectStorage<K> extends Storage<K, Object>, ObjectOperations<K> {

	default <T> T getAndTouch(Class<T> type, K key) {
		return getAndTouch(TypeDescriptor.valueOf(type), key);
	}

	default <T> T getAndTouch(TypeDescriptor type, K key) {
		T value = get(type, key);
		if (value != null) {
			touch(key);
		}
		return value;
	}
}
