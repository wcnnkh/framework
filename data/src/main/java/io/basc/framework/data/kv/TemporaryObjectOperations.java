package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryObjectOperations<K> extends ObjectOperations<K>, TemporaryKeyValueOperations<K, Object> {

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
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	default <T> void set(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	default <T> boolean setIfAbsent(K key, T value, Class<T> valueType, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	default <T> boolean setIfPresent(K key, Object value, Class<T> valueType, long exp, TimeUnit expUnit) {
		return setIfPresent(key, value, TypeDescriptor.valueOf(valueType), exp, expUnit);
	}

	boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);
}
