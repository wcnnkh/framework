package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T, E extends Throwable> {

	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return true;
	}

	void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E;
}
