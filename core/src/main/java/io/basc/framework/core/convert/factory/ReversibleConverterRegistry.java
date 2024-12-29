package io.basc.framework.core.convert.factory;

import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.util.Registration;

public interface ReversibleConverterRegistry<S, E extends Throwable> extends ReversibleConverterFactory<S, E> {
	<T> Registration registerReversibleConverter(Class<T> targetType, ReversibleConverter<S, T, ? extends E> converter);
}
