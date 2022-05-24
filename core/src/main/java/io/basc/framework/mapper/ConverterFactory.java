package io.basc.framework.mapper;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.lang.Nullable;

public interface ConverterFactory<S, E extends Throwable> extends TransformerFactory<S, E>, Converter<S, Object, E> {

	boolean isConverterRegistred(Class<?> type);

	@Nullable
	<T> Converter<S, T, E> getConverter(Class<? extends T> type);

	<T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter);

	default Object newInstance(TypeDescriptor type) {
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
