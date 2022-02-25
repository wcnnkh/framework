package io.basc.framework.data.storage;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyOperations;
import io.basc.framework.data.kv.TemporaryValueOperations;

/**
 * 临时存储
 * 
 * @author wcnnkh
 *
 */
public interface TemporaryStorage<K>
		extends Storage<K>, TemporaryKeyOperations<K>, TemporaryValueOperations<K, Object> {

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

	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> boolean setIfAbsent(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> boolean setIfPresent(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		return setIfPresent(key, valueType, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> void set(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return Storage.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return Storage.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		Storage.super.set(key, value);
	}
}
