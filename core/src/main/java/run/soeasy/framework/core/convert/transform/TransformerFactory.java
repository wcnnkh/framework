package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

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
	default boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws E {
		Transformer<S, Object, E> transformer = getTransformer(targetType.getType());
		if (transformer == null) {
			return false;
		}
		return transformer.transform(source, sourceType, target, targetType);
	}
}
