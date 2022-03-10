package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryObjectCasOperations<K>
		extends TemporaryObjectOperations<K>, TemporaryKeyValueCasOperations<K, Object>, ObjectCasOperations<K> {

	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return cas(key, value, TypeDescriptor.forObject(value), cas, exp, expUnit);
	}

	default <T> boolean cas(K key, T value, Class<T> valueType, long cas, long exp, TimeUnit expUnit) {
		return cas(key, valueType, TypeDescriptor.valueOf(valueType), cas, exp, expUnit);
	}

	boolean cas(K key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit);
}
