package io.basc.framework.convert;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionFactory;

public interface ConverterFactory<S, E extends Throwable> extends TransformerFactory<S, E>, Converter<S, Object, E> {

	boolean isConverterRegistred(Class<?> type);

	@Nullable
	<T> Converter<S, T, E> getConverter(Class<? extends T> type);

	<T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter);

	default Object newInstance(TypeDescriptor type) {
		if (type.isMap()) {
			return CollectionFactory.createMap(type.getType(), type.getMapKeyTypeDescriptor().getType(), 16);
		}

		if (type.isCollection()) {
			return CollectionFactory.createCollection(type.getType(), type.getElementTypeDescriptor().getType(), 16);
		}

		return ReflectionApi.newInstance(type.getType());
	}

	@SuppressWarnings("unchecked")
	@Override
	default <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		Converter<S, Object, E> converter = getConverter(targetType.getType());
		if (converter == null) {
			Object target = newInstance(targetType);
			transform(source, sourceType, target, targetType);
			return (R) target;
		}
		return converter.convert(source, sourceType, targetType);
	}
}
