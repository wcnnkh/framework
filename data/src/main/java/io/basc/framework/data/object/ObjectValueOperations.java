package io.basc.framework.data.object;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.ValueOperations;

public interface ObjectValueOperations<K> extends ValueOperations<K, Object> {

	@Override
	default boolean setIfPresent(K key, Object value) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value));
	}

	default <T> boolean setIfPresent(K key, T value, Class<T> valueType) {
		return setIfPresent(key, value, TypeDescriptor.valueOf(valueType));
	}

	boolean setIfPresent(K key, Object value, TypeDescriptor valueType);

	@Override
	default void set(K key, Object value) {
		set(key, value, TypeDescriptor.forObject(value));
	}

	default <T> void set(K key, T value, Class<T> valueType) {
		set(key, value, TypeDescriptor.valueOf(valueType));
	}

	void set(K key, Object value, TypeDescriptor valueType);

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value));
	}

	default <T> boolean setIfAbsent(K key, T value, Class<T> valueType) {
		return setIfAbsent(key, value, TypeDescriptor.valueOf(valueType));
	}

	boolean setIfAbsent(K key, Object value, TypeDescriptor valueType);
}
