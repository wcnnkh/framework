package io.basc.framework.convert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleConverterFactory<S, E extends Throwable> extends SimpleReversibleTransformerFactory<S, E>
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

	@Override
	public final <R> R convert(S source, Class<? extends R> targetType) throws E {
		return ConverterFactory.super.convert(source, targetType);
	}

	@Override
	public final <R> R convert(S source, Class<? extends S> sourceType, Class<? extends R> targetType) throws E {
		return ConverterFactory.super.convert(source, sourceType, targetType);
	}

	@Override
	public final <R> R convert(S source, Class<? extends S> sourceType, TypeDescriptor targetType) throws E {
		return ConverterFactory.super.convert(source, sourceType, targetType);
	}

	@Override
	public final <R> R convert(S source, TypeDescriptor sourceType, Class<? extends R> targetType) throws E {
		return ConverterFactory.super.convert(source, sourceType, targetType);
	}

	@Override
	public final Object convert(S source, TypeDescriptor targetType) throws E {
		return ConverterFactory.super.convert(source, targetType);
	}
}
