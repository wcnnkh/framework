package io.basc.framework.core.convert.config;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.util.exchange.Registration;

public interface ConverterRegistry<S, E extends Throwable> extends ConverterFactory<S, E> {
	<T> Registration registerConverter(Class<T> targetType, Converter<? super S, ? extends T, ? extends E> converter);
}
