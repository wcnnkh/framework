package io.basc.framework.data;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.KeyValueOperations;
import io.basc.framework.data.object.ObjectOperations;
import io.basc.framework.env.Sys;

@SuppressWarnings("unchecked")
public interface DataOperations<K> extends KeyValueOperations<K, Object>, ObjectOperations<K> {

	default ConversionService getConversionService() {
		return Sys.env.getConversionService();
	}

	@Override
	default <T> T get(TypeDescriptor type, K key) {
		Object value = get(key);
		return (T) getConversionService().convert(value, TypeDescriptor.forObject(value), type);
	}

	@Override
	default void set(K key, Object value) {
		ObjectOperations.super.set(key, value);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectOperations.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectOperations.super.setIfPresent(key, value);
	}
}
