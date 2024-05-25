package io.basc.framework.mapper.transform.factory;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.transform.ReversibleTransformer;

@FunctionalInterface
public interface ReversibleTransformerFactory<S, E extends Throwable>
		extends ReversibleTransformer<S, Object, E>, TransformerFactory<S, E> {
	@Override
	<T> ReversibleTransformer<S, T, E> getTransformer(Class<? extends T> requiredType);

	@Override
	default boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getTransformer(sourceType.getType()) != null;
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		ReversibleTransformer<S, Object, E> transformer = getTransformer(sourceType.getType());
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		transformer.reverseTransform(source, sourceType, target, targetType);
	}
}
