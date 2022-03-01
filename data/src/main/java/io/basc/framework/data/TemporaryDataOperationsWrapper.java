package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryKeyValueOperationsWrapper;
import io.basc.framework.data.object.TemporaryObjectOperationsWrapper;

public interface TemporaryDataOperationsWrapper<K> extends TemporaryDataOperations<K>, DataOperationsWrapper<K>,
		TemporaryKeyValueOperationsWrapper<K, Object>, TemporaryObjectOperationsWrapper<K> {
	@Override
	TemporaryDataOperations<K> getSourceOperations();
}
