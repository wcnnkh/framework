package io.basc.framework.mapper;

import io.basc.framework.convert.ConverterFactoryWrapper;

public interface MapperFactoryWrapper<S, E extends Throwable>
		extends ConverterFactoryWrapper<S, E>, MapperFactory<S, E> {
	
	@Override
	MapperFactory<S, E> getSourceConverterFactory();
	
	@Override
	default <T> Mapper<S, T, E> getMapper(Class<? extends T> type) {
		return getSourceConverterFactory().getMapper(type);
	}

	@Override
	default boolean isMapperRegistred(Class<?> type) {
		return getSourceConverterFactory().isMapperRegistred(type);
	}

	@Override
	default <T> void registerMapper(Class<T> type, Mapper<S, ? extends T, ? extends E> mapper) {
		getSourceConverterFactory().registerMapper(type, mapper);
	}
}
