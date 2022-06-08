package io.basc.framework.convert;

public interface ConverterFactoryWrapper<S, E extends Throwable>
		extends TransformerFactoryWrapper<S, E>, ConverterFactory<S, E> {

	@Override
	ConverterFactory<S, E> getSourceConverterFactory();

	@Override
	default boolean isConverterRegistred(Class<?> type) {
		return getSourceConverterFactory().isConverterRegistred(type);
	}

	@Override
	default <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		return getSourceConverterFactory().getConverter(type);
	}

	@Override
	default <T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter) {
		getSourceConverterFactory().registerConverter(type, converter);
	}
}
