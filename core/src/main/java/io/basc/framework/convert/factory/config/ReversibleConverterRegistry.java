package io.basc.framework.convert.factory.config;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.factory.ReversibleConverterFactory;

public interface ReversibleConverterRegistry<S, E extends Throwable, C extends ReversibleConverter<S, ? extends Object, ? extends E>>
		extends ReversibleConverterFactory<S, E>, ConverterRegistry<S, E, C> {
}
