package io.basc.framework.core.convert.transform.factory;

import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Transformer;
import lombok.NonNull;

public interface TransformerFactory<S, E extends Throwable> extends Transformer<S, Object, E> {
	<T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType);

	@Override
	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return getTransformer(targetType.getType()) != null;
	}

	@Override
	default void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws E {
		Transformer<S, Object, E> transformer = getTransformer(targetType.getType());
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		transformer.transform(source, sourceType, target, targetType);
	}
}
