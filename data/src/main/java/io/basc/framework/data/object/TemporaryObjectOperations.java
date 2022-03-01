package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyOperations;

/**
 * 临时存储
 * 
 * @author wcnnkh
 *
 */
public interface TemporaryObjectOperations<K>
		extends ObjectOperations<K>, TemporaryKeyOperations<K>, TemporaryObjectValueOperations<K> {

	default <T> T getAndTouch(Class<T> type, K key) {
		return getAndTouch(TypeDescriptor.valueOf(type), key);
	}

	default <T> T getAndTouch(TypeDescriptor type, K key) {
		return getAndTouch(type, key, 0, TimeUnit.MILLISECONDS);
	}

	default <T> T getAndTouch(Class<T> type, K key, long exp, TimeUnit expUnit) {
		return getAndTouch(TypeDescriptor.valueOf(type), key, exp, expUnit);
	}

	default <T> T getAndTouch(TypeDescriptor type, K key, long exp, TimeUnit expUnit) {
		T value = get(type, key);
		if (value != null) {
			touch(key, exp, expUnit);
		}
		return value;
	}
}
