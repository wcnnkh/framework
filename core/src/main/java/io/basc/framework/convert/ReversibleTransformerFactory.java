package io.basc.framework.convert;

public interface ReversibleTransformerFactory<S, E extends Throwable>
		extends TransformerFactory<S, E>, ReverseTransformerFactory<S, E>, ReversibleTransformer<S, Object, E> {
	boolean isReversibleTransformerRegistred(Class<?> type);

	<T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type);

	<T> void registerReversibleTransformer(Class<T> type,
			ReversibleTransformer<S, ? extends T, ? extends E> transformer);
}