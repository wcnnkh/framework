package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TransformationService extends Transformer<Object, Object> {

	default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return canTransform(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return canTransform(TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
	}

	default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return canTransform(sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
	}

	@Override
	boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetTypeDescriptor);

	default boolean transform(@NonNull Object source, @NonNull Object target) {
		return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default boolean transform(@NonNull Object source, @NonNull Object target,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return transform(source, TypeDescriptor.forObject(source), target, targetTypeDescriptor);
	}

	default <T> boolean transform(@NonNull Object source, @NonNull T target, @NonNull Class<? extends T> targetClass) {
		return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.valueOf(targetClass));
	}

	default boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target) {
		return transform(source, sourceTypeDescriptor, target, TypeDescriptor.forObject(target));
	}

	default <T> boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull T target, @NonNull Class<? extends T> targetClass) {
		return transform(source, sourceTypeDescriptor, target, TypeDescriptor.valueOf(targetClass));
	}

	default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target) {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.forObject(target));
	}

	default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, targetTypeDescriptor);
	}

	default <S, T> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target,
			@NonNull Class<? extends T> targetClass) {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.valueOf(targetClass));
	}
}
