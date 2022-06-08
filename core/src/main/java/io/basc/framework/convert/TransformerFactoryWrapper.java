package io.basc.framework.convert;

public interface TransformerFactoryWrapper<S, E extends Throwable> extends TransformerFactory<S, E> {
	
	TransformerFactory<S, E> getSourceConverterFactory();

	@Override
	default <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		return getSourceConverterFactory().getTransformer(type);
	}

	@Override
	default boolean isTransformerRegistred(Class<?> type) {
		return getSourceConverterFactory().isTransformerRegistred(type);
	}

	@Override
	default <T> void registerTransformer(Class<T> type, Transformer<S, ? extends T, ? extends E> transformer) {
		getSourceConverterFactory().registerTransformer(type, transformer);
	}
}
