package io.basc.framework.data;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.KeyValueCasOperationsWrapper;
import io.basc.framework.data.object.ObjectCasOperationsWrapper;

public interface DataCasOperationsWrapper<K> extends DataCasOperations<K>, KeyValueCasOperationsWrapper<K, Object>,
		DataOperationsWrapper<K>, ObjectCasOperationsWrapper<K> {

	@Override
	DataCasOperations<K> getSourceOperations();

	@Override
	default <T> CAS<T> gets(TypeDescriptor type, K key) {
		return DataCasOperations.super.gets(type, key);
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return ObjectCasOperationsWrapper.super.cas(key, value, cas);
	}
}
