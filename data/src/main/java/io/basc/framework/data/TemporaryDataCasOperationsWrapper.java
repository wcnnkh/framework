package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryObjectCasOperations;
import io.basc.framework.data.kv.TemporaryObjectCasOperationsWrapper;

public interface TemporaryDataCasOperationsWrapper extends TemporaryDataCasOperations,
		DataCasOperationsWrapper, TemporaryDataOperationsWrapper, TemporaryObjectCasOperationsWrapper<String> {
	@Override
	TemporaryObjectCasOperations<String> getSourceOperations();
}
