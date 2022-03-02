package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryValueCasOperations;

public interface TemporaryObjectValueCasOperations<K>
		extends TemporaryObjectValueOperations<K>, ObjectValueCasOperations<K>, TemporaryValueCasOperations<K, Object> {

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		return cas(key, value, valueType, cas, 9, TimeUnit.MILLISECONDS);
	}

	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return cas(key, value, TypeDescriptor.forObject(value), cas, exp, expUnit);
	}

	default <T> boolean cas(K key, T value, Class<T> valueType, long cas, long exp, TimeUnit expUnit) {
		return cas(key, valueType, TypeDescriptor.valueOf(valueType), cas, exp, expUnit);
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return ObjectValueCasOperations.super.cas(key, value, cas);
	}

	boolean cas(K key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit);
}
