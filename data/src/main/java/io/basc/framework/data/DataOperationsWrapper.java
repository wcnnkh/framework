package io.basc.framework.data;

import io.basc.framework.data.kv.ObjectOperations;
import io.basc.framework.data.kv.ObjectOperationsWrapper;

public interface DataOperationsWrapper extends DataOperations, ObjectOperationsWrapper<String> {

	@Override
	ObjectOperations<String> getSourceOperations();
}
