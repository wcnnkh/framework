package run.soeasy.framework.core.convert;

import lombok.NonNull;

@FunctionalInterface
public interface Convertable {

	default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return canConvert(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	default boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return canConvert(TypeDescriptor.valueOf(sourceClass), targetTypeDescriptor);
	}

	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return canConvert(sourceTypeDescriptor, TypeDescriptor.valueOf(targetClass));
	}

	boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetTypeDescriptor);
}
