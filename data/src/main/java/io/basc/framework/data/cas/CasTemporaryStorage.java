package io.basc.framework.data.cas;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyOperations;
import io.basc.framework.data.storage.TemporaryStorage;

public interface CasTemporaryStorage<K>
		extends TemporaryStorage<K>, CasStorage<K>, TemporaryKeyOperations<K>, CasTemporaryValueOperations<K, Object> {

	@Override
	default boolean cas(K key, Object value, long cas) {
		return CasStorage.super.cas(key, value, cas);
	}

	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		return cas(key, value, valueType, cas, 0, TimeUnit.MILLISECONDS);
	}

	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return cas(key, value, TypeDescriptor.forObject(value), cas, exp, expUnit);
	}

	default <T> boolean cas(K key, T value, Class<T> valueType, long cas, long exp, TimeUnit expUnit) {
		return cas(key, value, TypeDescriptor.valueOf(valueType), cas, exp, expUnit);
	}

	boolean cas(K key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit);

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		set(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return setIfAbsent(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return setIfPresent(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}
}
