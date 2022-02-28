package io.basc.framework.data;

import io.basc.framework.codec.Codec;

public interface DataOperationsWrapper<K>
		extends DataOperations<K>, KeyValueOperationsWrapper<K, Object>, ObjectOperationsWrapper<K> {
	@Override
	DataOperations<K> getSourceOperations();

	@Override
	default Codec<K, K> getKeyFomatter() {
		return ObjectOperationsWrapper.super.getKeyFomatter();
	}
}
