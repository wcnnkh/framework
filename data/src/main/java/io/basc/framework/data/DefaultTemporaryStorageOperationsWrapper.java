package io.basc.framework.data;

public class DefaultTemporaryStorageOperationsWrapper<W extends TemporaryStorageOperations>
		extends DefaultStorageOperationsWrapper<W> implements TemporaryStorageOperationsWrapper {

	public DefaultTemporaryStorageOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public TemporaryDataOperations<String> getSourceOperations() {
		return wrappedTarget;
	}
}
