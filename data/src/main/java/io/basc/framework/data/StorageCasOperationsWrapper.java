package io.basc.framework.data;

public interface StorageCasOperationsWrapper
		extends StorageCasOperations, DataCasOperationsWrapper<String>, StorageOperationsWrapper {

	@Override
	DataCasOperations<String> getSourceOperations();

}
