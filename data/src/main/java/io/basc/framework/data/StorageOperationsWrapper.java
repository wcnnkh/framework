package io.basc.framework.data;

public interface StorageOperationsWrapper extends StorageOperations, DataOperationsWrapper<String> {

	@Override
	DataOperations<String> getSourceOperations();
}
