package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyValueOperations;
import io.basc.framework.data.object.TemporaryObjectOperations;

public interface TemporaryDataOperations<K>
		extends DataOperations<K>, TemporaryKeyValueOperations<K, Object>, TemporaryObjectOperations<K> {

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
}
