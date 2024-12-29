package io.basc.framework.core.convert.transform.factory;

import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ReversibleTransformer;
import io.basc.framework.core.convert.transform.Transformer;
import lombok.NonNull;

public interface ReversibleTransformerFactory<S, E extends Throwable>
		extends ReversibleTransformer<S, Object, E>, TransformerFactory<S, E> {
	<T> ReversibleTransformer<S, T, E> getReversibleTransformer(@NonNull Class<? extends T> requiredType);

	@Override
	default <T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType) {
		return getReversibleTransformer(requiredType);
	}

	@Override
	default boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getReversibleTransformer(sourceType.getType()) != null;
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		ReversibleTransformer<S, Object, E> reversibleTransformer = getReversibleTransformer(sourceType.getType());
		if (reversibleTransformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		reversibleTransformer.reverseTransform(source, sourceType, target, targetType);
	}
}
