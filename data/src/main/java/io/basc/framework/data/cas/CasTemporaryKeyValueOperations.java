package io.basc.framework.data.cas;

import io.basc.framework.data.kv.TemporaryKeyValueOperations;

public interface CasTemporaryKeyValueOperations<K, V> extends CasKeyValueOperations<K, V>,
		TemporaryKeyValueOperations<K, V>, CasTemporaryKeyOperations<K>, CasTemporaryValueOperations<K, V> {

}
