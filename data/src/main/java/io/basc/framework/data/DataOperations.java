package io.basc.framework.data;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
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
}
