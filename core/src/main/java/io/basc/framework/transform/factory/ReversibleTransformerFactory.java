package io.basc.framework.transform.factory;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.transform.ReversibleTransformer;
import io.basc.framework.convert.transform.Transformer;

@FunctionalInterface
public interface ReversibleTransformerFactory<S, E extends Throwable>
		extends ReversibleTransformer<S, Object, E>, TransformerFactory<S, E> {

	@Override
	default <T> Transformer<S, T, E> getTransformer(Class<? extends T> requiredType) {
		return getReversibleTransformer(requiredType);
	}

	<T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> requiredType);

	@Override
	default boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		ReversibleTransformer<S, Object, E> transformer = getReversibleTransformer(sourceType.getType());
		if (transformer == null) {
			return false;
		}

		return transformer.canReverseTransform(sourceType, targetType);
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		ReversibleTransformer<S, Object, E> transformer = getReversibleTransformer(sourceType.getType());
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		transformer.reverseTransform(source, sourceType, target, targetType);
	}
}
