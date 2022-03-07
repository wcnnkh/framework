package io.basc.framework.data;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.KeyValueCasOperations;
import io.basc.framework.data.object.ObjectCasOperations;

public interface DataCasOperations<K>
		extends KeyValueCasOperations<K, Object>, DataOperations<K>, ObjectCasOperations<K> {

	@SuppressWarnings("unchecked")
	@Override
	default <T> CAS<T> gets(TypeDescriptor type, K key) {
		CAS<Object> value = gets(key);
		if (value == null) {
			return null;
		}
		return new CAS<T>(value.getCas(),
				(T) getConversionService().convert(value.getValue(), TypeDescriptor.forObject(value.getValue()), type));
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return cas(key, value, TypeDescriptor.forObject(value), cas);
	}
}
