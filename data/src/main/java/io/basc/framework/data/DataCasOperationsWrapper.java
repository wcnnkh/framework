package io.basc.framework.data;

import io.basc.framework.data.kv.KeyValueCasOperationsWrapper;
import io.basc.framework.data.object.ObjectCasOperationsWrapper;

public interface DataCasOperationsWrapper<K> extends DataCasOperations<K>, KeyValueCasOperationsWrapper<K, Object>,
		DataOperationsWrapper<K>, ObjectCasOperationsWrapper<K> {

	@Override
	DataCasOperations<K> getSourceOperations();
}
