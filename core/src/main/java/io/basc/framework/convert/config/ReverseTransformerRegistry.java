package io.basc.framework.convert.config;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.ReverseTransformer;
import io.basc.framework.convert.TypeDescriptor;

public interface ReverseTransformerRegistry<S, E extends Throwable> extends ReverseTransformer<Object, S, E> {
	default boolean isReverseTransformerRegistred(Class<?> type) {
		return getReverseTransformer(type) != null;
	}

	<T> ReverseTransformer<T, S, E> getReverseTransformer(Class<? extends T> type);

	<T> void registerReverseTransformer(Class<T> type,
			ReverseTransformer<? super T, ? super S, ? extends E> transformer);

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		ReverseTransformer<Object, S, E> reverseTransformer = getReverseTransformer(sourceType.getType());
		if (reverseTransformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		reverseTransformer.reverseTransform(source, sourceType, target, targetType);
	}
}