package io.basc.framework.mapper;

import io.basc.framework.convert.ReversibleConverterFactoryWrapper;

public interface ReversibleMapperFactoryWrapper<S, E extends Throwable>
		extends MapperFactoryWrapper<S, E>, ReversibleConverterFactoryWrapper<S, E>, ReversibleMapperFactory<S, E> {
	@Override
	ReversibleMapperFactory<S, E> getSourceConverterFactory();

	@Override
	default <T> ReversibleMapper<S, T, E> getReversibleMapper(Class<? extends T> type) {
		return getSourceConverterFactory().getReversibleMapper(type);
	}

	@Override
	default boolean isReversibleMapperRegistred(Class<?> type) {
		return getSourceConverterFactory().isReversibleMapperRegistred(type);
	}

	@Override
	default <T> void registerReversibleMapper(Class<T> type, ReversibleMapper<S, ? extends T, ? extends E> mapper) {
		getSourceConverterFactory().registerReversibleMapper(type, mapper);
	}
}
