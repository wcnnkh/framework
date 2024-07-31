package io.basc.framework.convert.factory.support;

import java.util.TreeMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.factory.config.ReversibleConverterRegistry;
import io.basc.framework.util.register.LimitedRegistration;
import io.basc.framework.util.register.Registration;

public class DefaultReversibleConverterFactory<S, E extends Throwable> extends DefaultConverterFactory<S, E>
		implements ReversibleConverterRegistry<S, E> {
	private TreeMap<Class<?>, ReversibleConverter<? super S, ?, ? extends E>> reversibleConverterMap;

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> requiredType) {
		return (ReversibleConverter<S, T, E>) get(requiredType, reversibleConverterMap);
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
		this.reversibleConverterMap = register(targetType, converter, reversibleConverterMap);
		return LimitedRegistration.of(() -> reversibleConverterMap.remove(targetType));
	}
}
