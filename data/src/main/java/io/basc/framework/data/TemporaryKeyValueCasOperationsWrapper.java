package io.basc.framework.data;

public interface TemporaryKeyValueCasOperationsWrapper<K, V> extends TemporaryKeyValueCasOperations<K, V>,
		TemporaryValueCasOperationsWrapper<K, V>, KeyValueCasOperationsWrapper<K, V>,
		TemporaryKeyCasOperationsWrapper<K>, TemporaryKeyValueOperationsWrapper<K, V> {

	@Override
	TemporaryKeyValueCasOperations<K, V> getSourceOperations();
}
