package io.basc.framework.convert;

public interface ReversibleTransformerFactoryWrapper<S, E extends Throwable>
		extends TransformerFactoryWrapper<S, E>, ReversibleTransformerFactory<S, E> {

	@Override
	ReversibleTransformerFactory<S, E> getSourceConverterFactory();

	@Override
	default <T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type) {
		return getSourceConverterFactory().getReversibleTransformer(type);
	}

	@Override
	default boolean isReversibleTransformerRegistred(Class<?> type) {
		return getSourceConverterFactory().isReversibleTransformerRegistred(type);
	}

	@Override
	default <T> void registerReversibleTransformer(Class<T> type,
			ReversibleTransformer<S, ? extends T, ? extends E> transformer) {
		getSourceConverterFactory().registerReversibleTransformer(type, transformer);
	}

}
