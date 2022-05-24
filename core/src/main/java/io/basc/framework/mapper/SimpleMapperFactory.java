package io.basc.framework.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.convert.Converter;
import io.basc.framework.util.Assert;

public class SimpleMapperFactory<S, E extends Throwable> extends SimpleConverterFactory<S, E>
		implements MapperFactory<S, E> {
	private final Map<Class<?>, Mapper<S, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isMapperRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Mapper<S, T, E> getMapper(Class<? extends T> type) {
		return (Mapper<S, T, E>) map.get(type);
	}

	@Override
	public <T> void registerMapper(Class<T> type, Mapper<S, ? extends T, ? extends E> mapper) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(mapper != null, "mapper");
		map.put(type, mapper);
	}

	@Override
	public boolean isConverterRegistred(Class<?> type) {
		return super.isConverterRegistred(type) || isMapperRegistred(type);
	}

	@Override
	public boolean isTransformerRegistred(Class<?> type) {
		return super.isTransformerRegistred(type) || isMapperRegistred(type);
	}

	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		Converter<S, T, E> converter = super.getConverter(type);
		if (converter == null) {
			converter = getMapper(type);
		}
		return converter;
	}

	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		Transformer<S, T, E> transformer = super.getTransformer(type);
		if (transformer == null) {
			transformer = getMapper(type);
		}
		return transformer;
	}
}
