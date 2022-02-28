package io.basc.framework.data;

public interface TemporaryDataOperationsWrapper<K> extends TemporaryDataOperations<K>, DataOperationsWrapper<K>,
		TemporaryKeyValueOperationsWrapper<K, Object>, TemporaryObjectOperationsWrapper<K> {
	@Override
	TemporaryDataOperations<K> getSourceOperations();
}
