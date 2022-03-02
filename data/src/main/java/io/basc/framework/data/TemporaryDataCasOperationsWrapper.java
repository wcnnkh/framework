package io.basc.framework.data;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyValueCasOperationsWrapper;
import io.basc.framework.data.object.TemporaryObjectCasOperationsWrapper;

public interface TemporaryDataCasOperationsWrapper<K>
		extends TemporaryDataCasOperations<K>, TemporaryDataOperationsWrapper<K>, DataCasOperationsWrapper<K>,
		TemporaryKeyValueCasOperationsWrapper<K, Object>, TemporaryObjectCasOperationsWrapper<K> {
	@Override
	TemporaryDataCasOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return TemporaryObjectCasOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return TemporaryObjectCasOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		TemporaryObjectCasOperationsWrapper.super.set(key, value);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return TemporaryDataOperationsWrapper.super.setIfAbsent(key, value, valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return TemporaryDataOperationsWrapper.super.setIfPresent(key, value, valueType);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		TemporaryDataOperationsWrapper.super.set(key, value, valueType);
	}
}
