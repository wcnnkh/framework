package io.basc.framework.convert;

public interface Transformer<S, T, E extends Throwable> {

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

	/**
	 * 和{@link ReverseTransformer#reverseTransform(Object, TypeDescriptor, Object, TypeDescriptor)}的行为相反
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @throws E
	 */
	void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E;
}
