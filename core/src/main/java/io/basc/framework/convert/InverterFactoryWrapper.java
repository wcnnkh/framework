package io.basc.framework.convert;

public interface InverterFactoryWrapper<T, E extends Throwable>
		extends ReverseTransformerFactoryWrapper<T, E>, InverterFactory<T, E> {
	@Override
	InverterFactory<T, E> getSourceConverterFactory();

	@Override
	default <S> Inverter<S, T, E> getInverter(Class<? extends S> type) {
		return getSourceConverterFactory().getInverter(type);
	}

	@Override
	default boolean isInverterRegistred(Class<?> type) {
		return getSourceConverterFactory().isInverterRegistred(type);
	}

	@Override
	default <S> void registerInverter(Class<S> type, Inverter<? extends S, ? extends T, ? extends E> inverter) {
		getSourceConverterFactory().registerInverter(type, inverter);
	}
}
