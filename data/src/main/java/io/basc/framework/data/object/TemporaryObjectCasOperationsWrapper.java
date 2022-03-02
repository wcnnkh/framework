package io.basc.framework.data.object;

import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryObjectCasOperationsWrapper<K> extends TemporaryObjectCasOperations<K>,
		TemporaryObjectValueCasOperationsWrapper<K>, ObjectCasOperationsWrapper<K> {

	@Override
	TemporaryObjectCasOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return TemporaryObjectValueCasOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return TemporaryObjectValueCasOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		return TemporaryObjectValueCasOperationsWrapper.super.cas(key, value, valueType, cas);
	}

	@Override
	default void set(K key, Object value) {
		TemporaryObjectValueCasOperationsWrapper.super.set(key, value);
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return TemporaryObjectValueCasOperationsWrapper.super.cas(key, value, cas);
	}
}
