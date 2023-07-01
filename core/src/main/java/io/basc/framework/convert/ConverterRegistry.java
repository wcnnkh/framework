package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public interface ConverterRegistry<S, E extends Throwable> extends Converter<S, Object, E> {

	default boolean isConverterRegistred(Class<?> type) {
		return getConverter(type) != null;
	}

	@Nullable
	<T> Converter<S, T, E> getConverter(Class<? extends T> type);

	<T> void registerConverter(Class<T> type, Converter<? super S, ? extends T, ? extends E> converter);

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		Converter<S, Object, E> converter = getConverter(targetType.getType());
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.convert(source, sourceType, targetType);
	}
}
