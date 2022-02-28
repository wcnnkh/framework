package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 临时存储
 * 
 * @author wcnnkh
 *
 */
public interface TemporaryObjectOperations<K>
		extends ObjectOperations<K>, TemporaryKeyOperations<K>, TemporaryValueOperations<K, Object> {

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

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectOperations.super.setIfAbsent(key, value);
	}

	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> boolean setIfAbsent(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return setIfAbsent(key, valueType, 0, TimeUnit.MILLISECONDS);
	}

	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (exists(key)) {
			return false;
		}
		set(key, value, valueType, exp, expUnit);
		return true;
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectOperations.super.setIfPresent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> boolean setIfPresent(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		return setIfPresent(key, valueType, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return setIfPresent(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (!exists(key)) {
			return false;
		}
		set(key, value, valueType, exp, expUnit);
		return true;
	}

	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> void set(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default void set(K key, Object value) {
		ObjectOperations.super.set(key, value);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		set(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}
}
