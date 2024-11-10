package io.basc.framework.convert.transform;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;

public interface ReversibleTransformer<S, T, E extends Throwable> extends Transformer<S, T, E> {
	default boolean canReverseTransform(Class<?> sourceType, Class<?> targetType) {
		return canReverseTransform(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canReverseTransform(Class<?> sourceType, TypeDescriptor targetType) {
		return canReverseTransform(sourceType == null ? null : TypeDescriptor.valueOf(sourceType), targetType);
	}

	default boolean canReverseTransform(TypeDescriptor sourceType, Class<?> targetType) {
		return canReverseTransform(sourceType, TypeDescriptor.valueOf(targetType));
	}

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the
	 * {@code targetType}. The TypeDescriptors provide additional context about the
	 * source and target locations where conversion would occur, often object fields
	 * or property locations.
	 * <p>
	 * If this method returns {@code true}, it means
	 * {@link #convert(Object, TypeDescriptor, TypeDescriptor)} is capable of
	 * converting an instance of {@code sourceType} to {@code targetType}.
	 * <p>
	 * Special note on collections, arrays, and maps types: For conversion between
	 * collection, array, and map types, this method will return {@code true} even
	 * though a convert invocation may still generate a {@link ConversionException}
	 * if the underlying elements are not convertible. Callers are expected to
	 * handle this exceptional case when working with collections and maps.
	 * 
	 * @param sourceType context about the source type to convert from (may be
	 *                   {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed between the source and
	 *         target types, {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	default boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	default void reverseTransform(T source, Class<? extends T> sourceType, S target) throws E {
		reverseTransform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(T source, Class<? extends T> sourceType, S target, Class<? extends S> targetType)
			throws E {
		reverseTransform(source, TypeDescriptor.valueOf(sourceType), target, targetType);
	}

	default void reverseTransform(T source, Class<? extends T> sourceType, S target, TypeDescriptor targetType)
			throws E {
		reverseTransform(source, TypeDescriptor.valueOf(sourceType), target, targetType);
	}

	default void reverseTransform(T source, S target) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(T source, S target, Class<? extends S> targetType) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default void reverseTransform(T source, S target, TypeDescriptor targetType) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default void reverseTransform(T source, TypeDescriptor sourceType, S target) throws E {
		reverseTransform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(T source, TypeDescriptor sourceType, S target, Class<? extends S> targetType)
			throws E {
		reverseTransform(source, sourceType, target, TypeDescriptor.valueOf(targetType));
	}

	void reverseTransform(T source, TypeDescriptor sourceType, S target, TypeDescriptor targetType) throws E;
}
