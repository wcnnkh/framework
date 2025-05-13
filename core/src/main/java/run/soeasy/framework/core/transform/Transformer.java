package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T, E extends Throwable> {

	default boolean canTransform(@NonNull Class<? extends S> sourceClass, @NonNull Class<? extends T> targetClass) {
		return canTransform(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	default boolean canTransform(@NonNull Class<? extends S> sourceClass, @NonNull TypeDescriptor targetType) {
		return canTransform(TypeDescriptor.valueOf(sourceClass), targetType);
	}

	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull Class<? extends T> targetClass) {
		return canTransform(sourceType, TypeDescriptor.valueOf(targetClass));
	}

	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return true;
	}

	default boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target) throws E {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.forObject(target));
	}

	default boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target,
			@NonNull Class<? extends T> targetClass) throws E {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.valueOf(targetClass));
	}

	default boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, targetType);
	}

	default boolean transform(@NonNull S source, @NonNull T target) throws E {
		return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default boolean transform(@NonNull S source, @NonNull T target, @NonNull Class<? extends T> targetClass) throws E {
		return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.valueOf(targetClass));
	}

	default boolean transform(@NonNull S source, @NonNull T target, @NonNull TypeDescriptor targetType) throws E {
		return transform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target) throws E {
		return transform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull Class<? extends T> targetClass) throws E {
		return transform(source, sourceType, target, TypeDescriptor.valueOf(targetClass));
	}

	/**
	 * 执行传输
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @return 是否成功
	 * @throws E
	 */
	boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E;
}
