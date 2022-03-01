package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryValueOperations;

public interface TemporaryObjectValueOperations<K>
		extends ObjectValueOperations<K>, TemporaryValueOperations<K, Object> {

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectValueOperations.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectValueOperations.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		ObjectValueOperations.super.set(key, value);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		set(key, valueType, 0, TimeUnit.MILLISECONDS);
	}

	void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return setIfAbsent(key, value, 0, TimeUnit.MILLISECONDS);
	}

	boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return setIfPresent(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);
}
