package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryKeyValueCasOperations;
import io.basc.framework.data.object.TemporaryObjectCasOperations;

public interface TemporaryDataCasOperations<K> extends TemporaryDataOperations<K>, DataCasOperations<K>,
		TemporaryKeyValueCasOperations<K, Object>, TemporaryObjectCasOperations<K> {
}
