package run.soeasy.framework.core.transform.config;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.Transformer;

public interface TransformerFactory<S, E extends Throwable> extends Transformer<S, Object, E> {
	<T> Transformer<S, T, E> getTransformer(@NonNull Class<? extends T> requiredType);

	@Override
	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return getTransformer(targetType.getType()) != null;
	}

	default void transform(@NonNull S source, @NonNull Object target) throws E {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
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
