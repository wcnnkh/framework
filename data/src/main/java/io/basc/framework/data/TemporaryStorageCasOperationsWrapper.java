package io.basc.framework.data;

import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryStorageCasOperationsWrapper extends TemporaryStorageCasOperations,
		StorageCasOperationsWrapper, TemporaryStorageOperationsWrapper, TemporaryDataCasOperations<String> {
	@Override
	TemporaryDataCasOperations<String> getSourceOperations();

	@Override
	default boolean setIfAbsent(String key, Object value, TypeDescriptor valueType) {
		return TemporaryStorageCasOperations.super.setIfAbsent(key, value, valueType);
	}

	@Override
	default boolean setIfPresent(String key, Object value, TypeDescriptor valueType) {
		return TemporaryStorageCasOperations.super.setIfPresent(key, value, valueType);
	}

	@Override
	default boolean cas(String key, Object value, long cas) {
		return TemporaryStorageCasOperations.super.cas(key, value, cas);
	}

	@Override
	default boolean cas(String key, Object value, TypeDescriptor valueType, long cas) {
		return TemporaryStorageCasOperations.super.cas(key, value, valueType, cas);
	}

	@Override
	default void set(String key, Object value, TypeDescriptor valueType) {
		TemporaryStorageCasOperations.super.set(key, value, valueType);
	}

}
