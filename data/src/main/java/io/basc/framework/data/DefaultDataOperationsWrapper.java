package io.basc.framework.data;

import io.basc.framework.data.kv.DefaultObjectOperationsWrapper;

public class DefaultDataOperationsWrapper<W extends DataOperations> extends DefaultObjectOperationsWrapper<String, W>
		implements DataOperationsWrapper {

	public DefaultDataOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}
}
