package io.basc.framework.data;

public class DefaultTemporaryDataOperationsWrapper<W extends TemporaryDataOperations>
		extends DefaultDataOperationsWrapper<W> implements TemporaryDataOperationsWrapper {

	public DefaultTemporaryDataOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}
}
