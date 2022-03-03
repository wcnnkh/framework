package io.basc.framework.data;

public class DefaultStorageOperationsWrapper<W extends StorageOperations>
		extends DefaultDataOperationsWrapper<W, String> implements StorageOperationsWrapper {

	public DefaultStorageOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}
}
