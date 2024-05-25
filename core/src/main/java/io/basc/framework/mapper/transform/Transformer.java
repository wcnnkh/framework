package io.basc.framework.mapper.transform;

import io.basc.framework.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T, E extends Throwable> {
	default boolean canTransform(Class<?> sourceType, Class<?> targetType) {
		return canTransform(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canTransform(Class<?> sourceType, TypeDescriptor targetType) {
		return canTransform(sourceType == null ? null : TypeDescriptor.valueOf(sourceType), targetType);
	}

	default boolean canTransform(TypeDescriptor sourceType, Class<?> targetType) {
		return canTransform(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	default void transform(S source, Class<? extends S> sourceType, T target) throws E {
		transform(source, TypeDescriptor.valueOf(sourceType), target);
	}

	default void transform(S source, Class<? extends S> sourceType, T target, Class<? extends T> targetType) throws E {
		transform(source, TypeDescriptor.valueOf(sourceType), target, TypeDescriptor.valueOf(targetType));
	}

	default void transform(S source, Class<? extends S> sourceType, T target, TypeDescriptor targetType) throws E {
		transform(source, TypeDescriptor.valueOf(sourceType), target, targetType);
	}

	default void transform(S source, T target) throws E {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default void transform(S source, T target, Class<? extends T> targetType) throws E {
		transform(source, target, TypeDescriptor.valueOf(targetType));
	}

	default void transform(S source, T target, TypeDescriptor targetType) throws E {
		transform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default void transform(S source, TypeDescriptor sourceType, T target) throws E {
		transform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default void transform(S source, TypeDescriptor sourceType, T target, Class<? extends T> targetType) throws E {
		transform(source, sourceType, target, TypeDescriptor.valueOf(targetType));
	}

	void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E;
}
