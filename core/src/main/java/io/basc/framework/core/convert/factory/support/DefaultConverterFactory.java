package io.basc.framework.core.convert.factory.support;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.factory.config.ConverterRegistry;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.ServiceMap;

public class DefaultConverterFactory<S, E extends Throwable> implements ConverterRegistry<S, E> {
	private final ServiceMap<Converter<? super S, ? extends Object, ? extends E>> converterMap = new ServiceMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		return (Converter<S, T, E>) converterMap.getFirst(requiredType);
	}

	@Override
	public <T> Registration registerConverter(Class<T> targetType,
			Converter<? super S, ? extends T, ? extends E> converter) {
		return converterMap.register(targetType, converter);
	}
}
