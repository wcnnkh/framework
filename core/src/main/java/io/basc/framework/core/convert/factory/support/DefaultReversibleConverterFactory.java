package io.basc.framework.core.convert.factory.support;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.factory.config.ReversibleConverterRegistry;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.ServiceMap;

public class DefaultReversibleConverterFactory<S, E extends Throwable> extends DefaultConverterFactory<S, E>
		implements ReversibleConverterRegistry<S, E> {
	private final ServiceMap<ReversibleConverter<? super S, ?, ? extends E>> reversibleConverterMap = new ServiceMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> requiredType) {
		return (ReversibleConverter<S, T, E>) reversibleConverterMap.getFirst(requiredType);
	}

	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> requiredType) {
		Converter<S, T, E> converter = super.getConverter(requiredType);
		if (converter == null) {
			converter = getReversibleConverter(requiredType);
		}
		return converter;
	}

	@Override
	public <T> Registration registerReversibleConverter(Class<T> targetType,
			ReversibleConverter<S, T, ? extends E> converter) {
		return reversibleConverterMap.register(targetType, converter);
	}
}
