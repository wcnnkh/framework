package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public interface ReversibleConverter<S, T, E extends Throwable> extends Converter<S, T, E> {
	default boolean canReverseConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
		return canReverseConvert(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canReverseConvert(@Nullable Class<?> sourceType, TypeDescriptor targetType) {
		return canReverseConvert(sourceType == null ? null : TypeDescriptor.valueOf(sourceType), targetType);
	}

	default boolean canReverseConvert(@Nullable TypeDescriptor sourceType, Class<?> targetType) {
		return canReverseConvert(sourceType, TypeDescriptor.valueOf(targetType));
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
	default boolean canReverseConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	default <R extends S> R reverseConvert(@Nullable T source, Class<R> targetType) throws E {
		return reverseConvert(source, TypeDescriptor.forObject(source), targetType);
	}

	default <R extends S> R reverseConvert(@Nullable T source, @Nullable Class<? extends S> sourceType,
			Class<R> targetType) throws E {
		return reverseConvert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	@SuppressWarnings("unchecked")
	default <R extends S> R reverseConvert(@Nullable T source, @Nullable Class<? extends S> sourceType,
			TypeDescriptor targetType) throws E {
		return (R) reverseConvert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	default S reverseConvert(@Nullable T source, TypeDescriptor targetType) throws E {
		return reverseConvert(source, TypeDescriptor.forObject(source), targetType);
	}

	@SuppressWarnings("unchecked")
	default <R extends S> R reverseConvert(@Nullable T source, @Nullable TypeDescriptor sourceType, Class<R> targetType)
			throws E {
		return (R) reverseConvert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	S reverseConvert(@Nullable T source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}