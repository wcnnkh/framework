package io.basc.framework.data;

import io.basc.framework.data.kv.DefaultObjectOperationsWrapper;

public class DefaultDataStorageWrapper extends DefaultObjectOperationsWrapper<String, DataStorage>
		implements DataStorageWrapper {

	public DefaultDataStorageWrapper(DataStorage wrappedTarget) {
		super(wrappedTarget);
	}

}
