package io.basc.framework.convert;

public interface ReverseTransformerFactoryWrapper<T, E extends Throwable> extends ReverseTransformerFactory<T, E> {
	ReverseTransformerFactory<T, E> getSourceConverterFactory();

	@Override
	default boolean isReverseTransformerRegistred(Class<?> type) {
		return getSourceConverterFactory().isReverseTransformerRegistred(type);
	}

	@Override
	default <S> ReverseTransformer<S, T, E> getReverseTransformer(Class<? extends S> type) {
		return getSourceConverterFactory().getReverseTransformer(type);
	}

	@Override
	default <S> void registerReverseTransformer(Class<S> type,
			ReverseTransformer<? extends S, ? extends T, ? extends E> transformer) {
		getSourceConverterFactory().registerReverseTransformer(type, transformer);
	}
}
