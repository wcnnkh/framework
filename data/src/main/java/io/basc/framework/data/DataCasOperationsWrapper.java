package io.basc.framework.data;

public interface DataCasOperationsWrapper<K> extends DataCasOperations<K>, KeyValueCasOperationsWrapper<K, Object>,
		DataOperationsWrapper<K>, ObjectCasOperationsWrapper<K> {

	@Override
	DataCasOperations<K> getSourceOperations();
}
