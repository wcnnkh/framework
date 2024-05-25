package io.basc.framework.convert.factory.support;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.factory.config.ReversibleConverterRegistry;

public class DefaultReversibleConverterFactory<S, E extends Throwable, C extends ReversibleConverter<S, ? extends Object, ? extends E>>
		extends DefaultConverterFactory<S, E, C> implements ReversibleConverterRegistry<S, E, C> {
	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleConverter<S, T, E> getConverter(Class<? extends T> requiredType) {
		return (ReversibleConverter<S, T, E>) super.getConverter(requiredType);
	}
}
