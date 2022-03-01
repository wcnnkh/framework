package io.basc.framework.data.template;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.object.ObjectOperations;

public interface TemporaryObjectTemplate<K> extends TemporaryKeyTemplate<K>, ObjectOperations<K> {

	default <T> T getAndTouch(Class<T> type, K key) {
		T value = get(type, key);
		if (value == null) {
			return null;
		}
		touch(key);
		return value;
	}

	default <T> T getAndTouch(TypeDescriptor type, K key) {
		T value = get(type, key);
		if (value == null) {
			return null;
		}
		touch(key);
		return value;
	}
}
