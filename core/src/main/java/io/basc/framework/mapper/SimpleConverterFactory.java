package io.basc.framework.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.util.Assert;

public class SimpleConverterFactory<S, E extends Throwable> extends SimpleTransformerFactory<S, E>
		implements ConverterFactory<S, E> {
	private final Map<Class<?>, Converter<S, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isConverterRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		return (Converter<S, T, E>) map.get(type);
	}

	@Override
	public <T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(converter != null, "converter");
		map.put(type, converter);
	}
}
