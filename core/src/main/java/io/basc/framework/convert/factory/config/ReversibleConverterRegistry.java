package io.basc.framework.convert.factory.config;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.factory.ReversibleConverterFactory;
import io.basc.framework.util.Registration;

public interface ReversibleConverterRegistry<S, E extends Throwable> extends ReversibleConverterFactory<S, E> {
	<T> Registration registerReversibleConverter(Class<T> targetType, ReversibleConverter<S, T, ? extends E> converter);
}
