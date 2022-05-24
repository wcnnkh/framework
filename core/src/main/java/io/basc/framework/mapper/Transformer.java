package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

public interface Transformer<S, T, E extends Throwable> {
	default void transform(S source, T target) throws E {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default void transform(S source, TypeDescriptor sourceType, T target, Class<? extends T> targetType) throws E {
		transform(source, sourceType, target, TypeDescriptor.valueOf(targetType));
	}

	default void transform(S source, Class<? extends S> sourceType, T target) throws E {
		transform(source, TypeDescriptor.valueOf(sourceType), target);
	}

	default void transform(S source, T target, Class<? extends T> targetType) throws E {
		transform(source, target, TypeDescriptor.valueOf(targetType));
	}

	default void transform(S source, Class<? extends S> sourceType, T target, TypeDescriptor targetType) throws E {
		transform(source, TypeDescriptor.valueOf(sourceType), target, targetType);
	}

	default void transform(S source, TypeDescriptor sourceType, T target) throws E {
		transform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default void transform(S source, T target, TypeDescriptor targetType) throws E {
		transform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default void transform(S source, Class<? extends S> sourceType, T target, Class<? extends T> targetType) throws E {
		transform(source, TypeDescriptor.valueOf(sourceType), target, TypeDescriptor.valueOf(targetType));
	}

	void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E;
}
