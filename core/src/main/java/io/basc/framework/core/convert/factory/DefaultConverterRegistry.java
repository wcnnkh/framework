package io.basc.framework.core.convert.factory;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.ServiceMap;

public class DefaultConverterRegistry<S, E extends Throwable> extends ServiceMap<Converter<? super S, ?, ? extends E>>
		implements ConverterRegistry<S, E> {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		return (Converter<S, T, E>) getFirst(requiredType);
	}

	@Override
	public <T> Registration registerConverter(Class<T> requiredType,
			Converter<? super S, ? extends T, ? extends E> converter) {
		remove(requiredType);
		return register(requiredType, converter);
	}
}
