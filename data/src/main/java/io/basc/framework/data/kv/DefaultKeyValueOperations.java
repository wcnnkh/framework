package io.basc.framework.data.kv;

import io.basc.framework.codec.Codec;

public class DefaultKeyValueOperations<K, V, W extends KeyValueOperations<K, V>>
		extends DefaultKeyOperationsWrapper<K, Codec<K, K>, W> implements KeyValueOperationsWrapper<K, V> {

	public DefaultKeyValueOperations(W wrappedTarget) {
		super(wrappedTarget);
	}
}
