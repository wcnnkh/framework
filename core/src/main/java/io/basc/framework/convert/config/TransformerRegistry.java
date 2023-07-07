package io.basc.framework.convert.config;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.Transformer;
import io.basc.framework.convert.TypeDescriptor;

public interface TransformerRegistry<S, E extends Throwable> extends Transformer<S, Object, E> {

	default boolean isTransformerRegistred(Class<?> targetType) {
		return getTransformer(targetType) != null;
	}

	<T> Transformer<S, T, E> getTransformer(Class<? extends T> targetType);

	<T> void registerTransformer(Class<T> targetType, Transformer<? super S, ? super T, ? extends E> transformer);

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		Transformer<S, Object, E> transformer = getTransformer(targetType.getType());
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		transformer.transform(source, target, targetType);
	}

	default boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return targetType != null && isTransformerRegistred(targetType.getType());
	}
}
