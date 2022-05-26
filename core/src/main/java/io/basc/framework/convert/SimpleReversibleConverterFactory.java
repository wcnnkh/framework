package io.basc.framework.convert;

import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleReversibleConverterFactory<R, E extends Throwable> extends SimpleInverterFactory<R, E>
		implements ReversibleConverterFactory<R, E> {
	private final ConcurrentHashMap<Class<?>, ReversibleConverter<R, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isReversibleConverterRegistred(Class<?> type) {
		return map.contains(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleConverter<R, T, E> getReversibleConverter(Class<? extends T> type) {
		return (ReversibleConverter<R, T, E>) map.get(type);
	}

	@Override
	public <T> void registerReversibleConverter(Class<T> type,
			ReversibleConverter<R, ? extends T, ? extends E> converter) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(converter != null, "converter");
		map.put(type, converter);
	}

	@Override
	public boolean isConverterRegistred(Class<?> type) {
		return super.isConverterRegistred(type) || isReversibleConverterRegistred(type);
	}

	@Override
	public <T> Converter<R, T, E> getConverter(Class<? extends T> type) {
		Converter<R, T, E> converter = super.getConverter(type);
		if (converter != null) {
			return converter;
		}
		return getReversibleConverter(type);
	}

	@Override
	public boolean isInverterRegistred(Class<?> type) {
		return super.isInverterRegistred(type) || isReversibleConverterRegistred(type);
	}

	@Override
	public <S> Inverter<S, R, E> getInverter(Class<? extends S> type) {
		Inverter<S, R, E> inverter = super.getInverter(type);
		if (inverter != null) {
			return inverter;
		}
		return getReversibleConverter(type);
	}
}
