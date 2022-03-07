package io.basc.framework.data;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.KeyValueOperationsWrapper;
import io.basc.framework.data.object.ObjectOperationsWrapper;

public interface DataOperationsWrapper<K>
		extends DataOperations<K>, KeyValueOperationsWrapper<K, Object>, ObjectOperationsWrapper<K> {
	@Override
	DataOperations<K> getSourceOperations();

	@Override
	default Codec<K, K> getKeyFomatter() {
		return ObjectOperationsWrapper.super.getKeyFomatter();
	}

	@Override
	default Codec<Object, Object> getValueFomatter() {
		return KeyValueOperationsWrapper.super.getValueFomatter();
	}

	@Override
	default <T> T get(TypeDescriptor type, K key) {
		return DataOperations.super.get(type, key);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return DataOperations.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return DataOperations.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		DataOperations.super.set(key, value);
	}
}
