package io.basc.framework.data;

public interface TemporaryStorageCasOperationsWrapper extends TemporaryStorageCasOperations,
		StorageCasOperationsWrapper, TemporaryStorageOperationsWrapper, TemporaryDataCasOperationsWrapper<String> {
	@Override
	TemporaryDataCasOperations<String> getSourceOperations();
}
