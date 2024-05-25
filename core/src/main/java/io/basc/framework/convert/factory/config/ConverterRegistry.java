package io.basc.framework.convert.factory.config;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.factory.ConverterFactory;
import io.basc.framework.util.Registration;

public interface ConverterRegistry<S, E extends Throwable, C extends Converter<? super S, ? extends Object, ? extends E>>
		extends ConverterFactory<S, E> {
	Registration registerConverter(Class<?> requiredType, C converter);

	default boolean isConverterRegistred(Class<?> requiredType) {
		return getConverter(requiredType) != null;
	}
}
