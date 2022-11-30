package io.basc.framework.convert;

public interface ReversibleMapperFactory<S, E extends Throwable> extends MapperFactory<S, E>,
		ReversibleConverterFactory<S, E>, ReversibleTransformerFactory<S, E>, ReversibleMapper<S, Object, E> {
	default boolean isReversibleMapperRegistred(Class<?> type) {
		return getReversibleMapper(type) != null;
	}

	<T> ReversibleMapper<S, T, E> getReversibleMapper(Class<? extends T> type);

	<T> void registerReversibleMapper(Class<T> type, ReversibleMapper<S, T, ? extends E> mapper);
}
