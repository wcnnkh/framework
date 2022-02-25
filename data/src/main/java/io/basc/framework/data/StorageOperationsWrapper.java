package io.basc.framework.data;

import io.basc.framework.data.storage.TemporaryStorageWrapper;

public interface StorageOperationsWrapper extends StorageOperations, TemporaryStorageWrapper<String> {

	@Override
	StorageOperations getSourceOperations();
}
