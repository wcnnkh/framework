package io.basc.framework.convert;

public interface MapperFactory<S, E extends Throwable> extends ConverterFactory<S, E>, Mapper<S, Object, E> {

	default boolean isMapperRegistred(Class<?> type) {
		return getMapper(type) != null;
	}

	<T> Mapper<S, T, E> getMapper(Class<? extends T> type);

	<T> void registerMapper(Class<T> type, Mapper<S, T, ? extends E> mapper);
}