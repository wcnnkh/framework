package io.basc.framework.mapper;

public interface MapperFactory<S, E extends Throwable> extends ConverterFactory<S, E>, Mapper<S, Object, E> {

	boolean isMapperRegistred(Class<?> type);

	<T> Mapper<S, T, E> getMapper(Class<? extends T> type);

	<T> void registerMapper(Class<T> type, Mapper<S, ? extends T, ? extends E> mapper);
}