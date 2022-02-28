package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryObjectCasOperationsWrapper<K>
		extends TemporaryObjectCasOperations<K>, ObjectCasOperationsWrapper<K>, TemporaryValueCasOperationsWrapper<K, Object> {

	@Override
	TemporaryObjectCasOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryValueCasOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return TemporaryObjectCasOperations.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryValueCasOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return TemporaryObjectCasOperations.super.setIfPresent(key, value);
	}

	@Override
	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return TemporaryObjectCasOperations.super.cas(key, value, cas, exp, expUnit);
	}

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		return ObjectCasOperationsWrapper.super.cas(key, value, valueType, cas);
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return ObjectCasOperationsWrapper.super.cas(key, value, cas);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryValueCasOperationsWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value) {
		ObjectCasOperationsWrapper.super.set(key, value);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return ObjectCasOperationsWrapper.super.setIfAbsent(key, value, valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return ObjectCasOperationsWrapper.super.setIfPresent(key, value, valueType);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		ObjectCasOperationsWrapper.super.set(key, value, valueType);
	}
}
