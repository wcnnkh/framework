package io.basc.framework.convert;

public interface ReversibleTransformerFactory<S, E extends Throwable>
		extends TransformerFactory<S, E>, ReverseTransformerFactory<S, E>, ReversibleTransformer<S, Object, E> {
	default boolean isReversibleTransformerRegistred(Class<?> type) {
		return getReverseTransformer(type) != null;
	}

	<T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type);

	<T> void registerReversibleTransformer(Class<T> type, ReversibleTransformer<S, T, ? extends E> transformer);
}