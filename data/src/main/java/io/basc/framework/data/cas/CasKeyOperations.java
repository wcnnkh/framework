package io.basc.framework.data.cas;

import io.basc.framework.data.kv.KeyOperations;

public interface CasKeyOperations<K> extends KeyOperations<K> {
	boolean delete(K key, long cas);
}
