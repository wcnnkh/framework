package io.basc.framework.data.cas;

import io.basc.framework.data.kv.TemporaryKeyOperationsWrapper;

public interface CasTemporaryKeyOperationsWrapper<K>
		extends CasTemporaryKeyOperations<K>, CasKeyOperationsWrapper<K>, TemporaryKeyOperationsWrapper<K> {
	@Override
	CasTemporaryKeyOperations<K> getSourceOperations();
}
