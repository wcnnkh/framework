package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface ReversibleTransformer<S, T, E extends Throwable> extends Transformer<S, T, E> {
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
	default boolean canReverseTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return true;
	}

	default boolean canReverseTransform(@NonNull Class<? extends T> sourceClass, @NonNull TypeDescriptor targetType) {
		return canReverseTransform(TypeDescriptor.valueOf(sourceClass), targetType);
	}

	default boolean canReverseTransform(@NonNull TypeDescriptor sourceType, @NonNull Class<? extends S> targetClass) {
		return canReverseTransform(sourceType, TypeDescriptor.valueOf(targetClass));
	}

	default boolean canReverseTransform(@NonNull Class<? extends T> sourceClass,
			@NonNull Class<? extends S> targetClass) {
		return canReverseTransform(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceType, S target,
			@NonNull TypeDescriptor targetType) throws E;

	default boolean reverseTransform(@NonNull T source, @NonNull Class<? extends T> sourceClass, @NonNull S target,
			@NonNull TypeDescriptor targetType) throws E {
		return reverseTransform(source, TypeDescriptor.valueOf(sourceClass), target, targetType);
	}

	default boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceType, @NonNull S target,
			@NonNull Class<? extends S> targetClass) throws E {
		return reverseTransform(source, sourceType, target, TypeDescriptor.valueOf(targetClass));
	}

	default boolean reverseTransform(@NonNull T source, @NonNull Class<? extends T> sourceClass, @NonNull S target,
			@NonNull Class<? extends S> targetClass) throws E {
		return reverseTransform(source, TypeDescriptor.valueOf(sourceClass), target,
				TypeDescriptor.valueOf(targetClass));
	}

	default boolean reverseTransform(@NonNull T source, @NonNull S target, @NonNull TypeDescriptor targetType)
			throws E {
		return reverseTransform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default boolean reverseTransform(@NonNull T source, @NonNull S target, @NonNull Class<? extends S> targetClass)
			throws E {
		return reverseTransform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.valueOf(targetClass));
	}

	default boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceType, @NonNull S target)
			throws E {
		return reverseTransform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default boolean reverseTransform(@NonNull T source, @NonNull Class<? extends T> sourceClass, @NonNull S target)
			throws E {
		return reverseTransform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.forObject(target));
	}

	default boolean reverseTransform(@NonNull T source, @NonNull S target) throws E {
		return reverseTransform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}
}
