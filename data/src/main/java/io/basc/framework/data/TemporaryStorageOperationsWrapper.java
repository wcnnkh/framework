package io.basc.framework.data;

public interface TemporaryStorageOperationsWrapper
		extends TemporaryStorageOperations, StorageOperationsWrapper, TemporaryDataOperationsWrapper<String> {
	@Override
	TemporaryDataOperations<String> getSourceOperations();
}
