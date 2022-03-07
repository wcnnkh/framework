package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

public interface TemporaryStorageOperationsWrapper
		extends TemporaryStorageOperations, StorageOperationsWrapper, TemporaryDataOperationsWrapper<String> {
	@Override
	TemporaryDataOperations<String> getSourceOperations();

	@Override
	default boolean setIfAbsent(String key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryDataOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(String key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryDataOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default void set(String key, Object value, long exp, TimeUnit expUnit) {
		TemporaryDataOperationsWrapper.super.set(key, value, exp, expUnit);
	}
}
