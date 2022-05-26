package io.basc.framework.convert;

public interface ReversibleConverterFactoryWrapper<S, E extends Throwable>
		extends ConverterFactoryWrapper<S, E>, InverterFactoryWrapper<S, E>, ReversibleConverterFactory<S, E> {
	@Override
	ReversibleConverterFactory<S, E> getSourceConverterFactory();

	@Override
	default <T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> type) {
		return getSourceConverterFactory().getReversibleConverter(type);
	}

	@Override
	default boolean isReversibleConverterRegistred(Class<?> type) {
		return getSourceConverterFactory().isReversibleConverterRegistred(type);
	}

	@Override
	default <T> void registerReversibleConverter(Class<T> type,
			ReversibleConverter<S, ? extends T, ? extends E> converter) {
		getSourceConverterFactory().registerReversibleConverter(type, converter);
	}
}
