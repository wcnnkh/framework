package io.basc.framework.data;

import io.basc.framework.data.kv.ObjectCasOperations;
import io.basc.framework.data.kv.ObjectCasOperationsWrapper;

public interface DataCasOperationsWrapper
		extends DataCasOperations, ObjectCasOperationsWrapper<String>, DataOperationsWrapper {

	@Override
	ObjectCasOperations<String> getSourceOperations();

}
