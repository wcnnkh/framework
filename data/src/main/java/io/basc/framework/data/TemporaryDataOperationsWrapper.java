package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryObjectOperations;
import io.basc.framework.data.kv.TemporaryObjectOperationsWrapper;

public interface TemporaryDataOperationsWrapper
		extends TemporaryDataOperations, DataOperationsWrapper, TemporaryObjectOperationsWrapper<String> {
	@Override
	TemporaryObjectOperations<String> getSourceOperations();
}
