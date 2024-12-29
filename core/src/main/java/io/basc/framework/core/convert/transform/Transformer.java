package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.NonNull;

@FunctionalInterface
public interface Transformer<S, T, E extends Throwable> {

	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return true;
	}

	void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E;
}
