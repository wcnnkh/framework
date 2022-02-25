package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.cas.CasTemporaryStorageWrapper;

public interface CasStorageOperationsWrapper
		extends CasStorageOperations, CasTemporaryStorageWrapper<String>, StorageOperationsWrapper {

	@Override
	CasStorageOperations getSourceOperations();

	@Override
	default boolean setIfAbsent(String key, Object value, long exp, TimeUnit expUnit) {
		return CasTemporaryStorageWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(String key, Object value) {
		return CasTemporaryStorageWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(String key, Object value, long exp, TimeUnit expUnit) {
		return CasTemporaryStorageWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(String key, Object value) {
		return CasTemporaryStorageWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(String key, Object value, long exp, TimeUnit expUnit) {
		CasTemporaryStorageWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default void set(String key, Object value) {
		CasTemporaryStorageWrapper.super.set(key, value);
	}
}
