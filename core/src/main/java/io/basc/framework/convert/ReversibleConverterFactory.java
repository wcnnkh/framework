package io.basc.framework.convert;

public interface ReversibleConverterFactory<S, E extends Throwable>
		extends InverterFactory<S, E>, ConverterFactory<S, E>, ReversibleConverter<S, Object, E> {

	default boolean isReversibleConverterRegistred(Class<?> type) {
		return getReversibleConverter(type) != null;
	}

	<T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type);

	<T> void registerReversibleConverter(Class<T> type, ReversibleConverter<S, T, ? extends E> converter);
}
