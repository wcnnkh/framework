package io.basc.framework.core.convert.factory.config;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.factory.ConverterFactory;
import io.basc.framework.util.Registration;

public interface ConverterRegistry<S, E extends Throwable> extends ConverterFactory<S, E> {
	<T> Registration registerConverter(Class<T> targetType, Converter<? super S, ? extends T, ? extends E> converter);
}
