package io.basc.framework.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.ReversibleTransformer;
import io.basc.framework.util.Assert;

public class SimpleReverseMapperFactory<S, E extends Throwable> extends SimpleMapperFactory<S, E>
		implements ReversibleMapperFactory<S, E> {
	private final Map<Class<?>, ReversibleMapper<S, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isReversibleMapperRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleMapper<S, T, E> getReversibleMapper(Class<? extends T> type) {
		return (ReversibleMapper<S, T, E>) map.get(type);
	}

	@Override
	public <T> void registerReversibleMapper(Class<T> type, ReversibleMapper<S, ? extends T, ? extends E> mapper) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(mapper != null, "mapper");
		map.put(type, mapper);
	}

	@Override
	public boolean isReversibleConverterRegistred(Class<?> type) {
		return super.isReversibleConverterRegistred(type) || isReversibleMapperRegistred(type);
	}

	@Override
	public <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type) {
		ReversibleConverter<S, T, E> inverter = super.getReversibleConverter(type);
		if (inverter != null) {
			return inverter;
		}
		return getReversibleMapper(type);
	}

	@Override
	public boolean isMapperRegistred(Class<?> type) {
		return super.isMapperRegistred(type) || isReversibleMapperRegistred(type);
	}

	@Override
	public <T> Mapper<S, T, E> getMapper(Class<? extends T> type) {
		Mapper<S, T, E> mapper = super.getMapper(type);
		if (mapper != null) {
			return mapper;
		}

		return getReversibleMapper(type);
	}

	@Override
	public boolean isReversibleTransformerRegistred(Class<?> type) {
		return super.isReversibleTransformerRegistred(type) || isReversibleMapperRegistred(type);
	}

	@Override
	public <T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type) {
		ReversibleTransformer<S, T, E> transformer = super.getReversibleTransformer(type);
		if (transformer != null) {
			return transformer;
		}

		return getReversibleMapper(type);
	}
}
