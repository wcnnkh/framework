package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryValueOperations;

public interface TemporaryObjectValueOperations<K>
		extends ObjectValueOperations<K>, TemporaryValueOperations<K, Object> {

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		set(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfAbsent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return setIfPresent(key, value, TypeDescriptor.forObject(value), exp, expUnit);
	}

	boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit);
}
