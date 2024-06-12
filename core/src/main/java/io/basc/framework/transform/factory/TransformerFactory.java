package io.basc.framework.transform.factory;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.transform.Transformer;

@FunctionalInterface
public interface TransformerFactory<S, E extends Throwable> extends Transformer<S, Object, E> {
	<T> Transformer<S, T, E> getTransformer(Class<? extends T> requiredType);

	@Override
	default boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		Transformer<S, Object, E> transformer = getTransformer(targetType.getType());
		if (transformer == null) {
			return false;
		}

		return transformer.canTransform(sourceType, targetType);
	}

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType) throws E {
		Transformer<S, Object, E> transformer = getTransformer(targetType.getType());
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		transformer.transform(source, sourceType, target, targetType);
	}
}
