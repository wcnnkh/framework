package io.basc.framework.data.cas;

import io.basc.framework.data.kv.TemporaryKeyValueOperationsWrapper;

public interface CasTemporaryKeyValueOperationsWrapper<K, V> extends CasTemporaryKeyValueOperations<K, V>,
		CasTemporaryValueOperationsWrapper<K, V>, CasKeyValueOperationsWrapper<K, V>,
		CasTemporaryKeyOperationsWrapper<K>, TemporaryKeyValueOperationsWrapper<K, V> {

	@Override
	CasTemporaryKeyValueOperations<K, V> getSourceOperations();
}
