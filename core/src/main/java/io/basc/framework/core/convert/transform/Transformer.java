package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T, E extends Throwable> {

	default boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E;
}
