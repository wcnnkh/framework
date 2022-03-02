package io.basc.framework.data;

import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryStorageCasOperationsWrapper extends TemporaryStorageCasOperations,
		StorageCasOperationsWrapper, TemporaryStorageOperationsWrapper, TemporaryDataCasOperations<String> {
	@Override
	TemporaryDataCasOperations<String> getSourceOperations();

	@Override
	default boolean cas(String key, Object value, long cas) {
		return TemporaryStorageCasOperations.super.cas(key, value, cas);
	}

	@Override
	default boolean cas(String key, Object value, TypeDescriptor valueType, long cas) {
		return TemporaryStorageCasOperations.super.cas(key, value, valueType, cas);
	}
}
