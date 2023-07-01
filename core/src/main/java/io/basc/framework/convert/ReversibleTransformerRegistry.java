package io.basc.framework.convert;

public interface ReversibleTransformerRegistry<S, E extends Throwable>
		extends TransformerRegistry<S, E>, ReverseTransformerRegistry<S, E>, ReversibleTransformer<S, Object, E> {
	default boolean isReversibleTransformerRegistred(Class<?> type) {
		return getReverseTransformer(type) != null;
	}

	<T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type);

	<T> void registerReversibleTransformer(Class<T> type,
			ReversibleTransformer<? super S, ? super T, ? extends E> transformer);
}