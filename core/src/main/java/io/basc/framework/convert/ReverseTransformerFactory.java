package io.basc.framework.convert;

public interface ReverseTransformerFactory<T, E extends Throwable> extends ReverseTransformer<Object, T, E> {
	default boolean isReverseTransformerRegistred(Class<?> type) {
		return getReverseTransformer(type) != null;
	}

	<S> ReverseTransformer<S, T, E> getReverseTransformer(Class<? extends S> type);

	<S> void registerReverseTransformer(Class<S> type, ReverseTransformer<S, T, ? extends E> transformer);

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, T target, TypeDescriptor targetType)
			throws E {
		ReverseTransformer<Object, T, E> reverser = getReverseTransformer(sourceType.getType());
		if (reverser == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		reverser.reverseTransform(source, sourceType, target, targetType);
	}
}