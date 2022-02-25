package io.basc.framework.data.cas;

import io.basc.framework.codec.Codec;
import io.basc.framework.data.kv.ValueOperationsWrapper;

public interface CasValueOperationsWrapper<K, V> extends CasValueOperations<K, V>, ValueOperationsWrapper<K, V> {
	CasValueOperations<K, V> getSourceOperations();

	@Override
	default boolean cas(K key, V value, long cas) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), cas);
	}
}
