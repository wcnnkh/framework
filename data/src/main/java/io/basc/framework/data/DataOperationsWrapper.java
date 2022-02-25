package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryKeyValueOperationsWrapper;

public interface DataOperationsWrapper extends DataOperations, TemporaryKeyValueOperationsWrapper<String, Object> {

	@Override
	DataOperations getSourceOperations();
}
