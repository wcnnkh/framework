package io.basc.framework.data.cas;

import io.basc.framework.codec.Codec;
import io.basc.framework.data.kv.KeyOperationsWrapper;

public interface CasKeyOperationsWrapper<K> extends CasKeyOperations<K>, KeyOperationsWrapper<K> {

	CasKeyOperations<K> getSourceOperations();

	@Override
	default boolean delete(K key, long cas) {
		Codec<K, K> fomatter = getKeyFomatter();
		return getSourceOperations().delete(fomatter == null ? key : fomatter.encode(key), cas);
	}
}
