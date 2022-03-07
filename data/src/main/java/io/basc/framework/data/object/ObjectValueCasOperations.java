package io.basc.framework.data.object;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.ValueCasOperations;

public interface ObjectValueCasOperations<K> extends ObjectValueOperations<K>, ValueCasOperations<K, Object> {

	@Override
	default boolean cas(K key, Object value, long cas) {
		return cas(key, value, TypeDescriptor.forObject(value), cas);
	}

	default <T> boolean cas(K key, T value, Class<T> valueType, long cas) {
		return cas(key, value, TypeDescriptor.valueOf(valueType), cas);
	}

	boolean cas(K key, Object value, TypeDescriptor valueType, long cas);
}
