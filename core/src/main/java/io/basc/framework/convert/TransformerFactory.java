package io.basc.framework.convert;

public interface TransformerFactory<S, E extends Throwable> extends Transformer<S, Object, E> {

	boolean isTransformerRegistred(Class<?> type);

	<T> Transformer<S, T, E> getTransformer(Class<? extends T> type);

	<T> void registerTransformer(Class<T> type, Transformer<S, ? extends T, ? extends E> transformer);

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		Transformer<S, Object, E> transformer = getTransformer(targetType.getType());
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		transformer.transform(source, target, targetType);
	}
}
