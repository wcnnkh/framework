package io.basc.framework.data.kv;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.codec.Codec;

public interface ObjectStorageWrapper<K>
		extends ObjectStorage<K>, StorageWrapper<K, Object>, ObjectOperationsWrapper<K> {

	@Override
	ObjectStorage<K> getSourceOperations();

	@SuppressWarnings("unchecked")
	@Override
	default <T> T getAndTouch(TypeDescriptor type, K key) throws UnsupportedOperationException {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		T value = getSourceOperations().getAndTouch(type, keyFomatter == null ? key : keyFomatter.encode(key));
		return valueFomatter == null ? value : (T) valueFomatter.decode(value);
	}
}
