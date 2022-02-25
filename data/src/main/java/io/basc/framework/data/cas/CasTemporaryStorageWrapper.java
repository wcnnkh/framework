package io.basc.framework.data.cas;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;

public interface CasTemporaryStorageWrapper<K>
		extends CasTemporaryStorage<K>, CasStorageWrapper<K>, CasTemporaryValueOperationsWrapper<K, Object> {

	@Override
	CasTemporaryStorage<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return CasTemporaryValueOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return CasTemporaryStorage.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return CasTemporaryValueOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return CasTemporaryStorage.super.setIfPresent(key, value);
	}

	@Override
	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return CasTemporaryStorage.super.cas(key, value, cas, exp, expUnit);
	}

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		return CasStorageWrapper.super.cas(key, value, valueType, cas);
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return CasStorageWrapper.super.cas(key, value, cas);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		CasTemporaryValueOperationsWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value) {
		CasStorageWrapper.super.set(key, value);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return CasStorageWrapper.super.setIfAbsent(key, value, valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return CasStorageWrapper.super.setIfPresent(key, value, valueType);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		CasStorageWrapper.super.set(key, value, valueType);
	}
}
