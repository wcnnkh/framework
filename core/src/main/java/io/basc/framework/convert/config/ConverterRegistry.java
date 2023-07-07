package io.basc.framework.convert.config;

import io.basc.framework.convert.ConversionTester;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;

public interface ConverterRegistry<S, E extends Throwable> extends Converter<S, Object, E>, ConversionTester {

	default boolean isConverterRegistred(Class<?> targetType) {
		return getConverter(targetType) != null;
	}

	@Nullable
	<T> Converter<S, T, E> getConverter(Class<? extends T> targetType);

	<T> void registerConverter(Class<T> targetType, Converter<? super S, ? extends T, ? extends E> converter);

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		Converter<S, Object, E> converter = getConverter(targetType.getType());
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.convert(source, sourceType, targetType);
	}
}
