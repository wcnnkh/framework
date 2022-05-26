package io.basc.framework.mapper;

import io.basc.framework.convert.ReversibleConverterFactory;

public interface ReversibleMapperFactory<S, E extends Throwable>
		extends MapperFactory<S, E>, ReversibleConverterFactory<S, E>, ReversibleMapper<S, Object, E> {
	boolean isReversibleMapperRegistred(Class<?> type);

	<T> ReversibleMapper<S, T, E> getReversibleMapper(Class<? extends T> type);

	<T> void registerReversibleMapper(Class<T> type, ReversibleMapper<S, ? extends T, ? extends E> mapper);
}
