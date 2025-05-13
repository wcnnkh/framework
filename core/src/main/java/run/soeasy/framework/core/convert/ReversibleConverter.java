package run.soeasy.framework.core.convert;

public interface ReversibleConverter<S, T, E extends Throwable> extends Converter<S, T, E> {
	default boolean canReverseConvert(Class<? extends T> sourceClass, TypeDescriptor targetType) {
		return canReverseConvert(TypeDescriptor.valueOf(sourceClass), targetType);
	}

	default boolean canReverseConvert(TypeDescriptor sourceType, Class<? extends S> targetClass) {
		return canReverseConvert(sourceType, TypeDescriptor.valueOf(targetClass));
	}

	default boolean canReverseConvert(Class<? extends T> sourceClass, Class<? extends S> targetClass) {
		return canReverseConvert(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
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
	default boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	default S reverseConvert(T source, Class<? extends S> targetClass) throws E {
		return reverseConvert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetClass));
	}

	default S reverseConvert(T source, TypeDescriptor targetType) throws E {
		return reverseConvert(source, TypeDescriptor.forObject(source), targetType);
	}

	default S reverseConvert(T source, TypeDescriptor sourceType, Class<? extends S> targetClass) throws E {
		return reverseConvert(source, sourceType, TypeDescriptor.valueOf(targetClass));
	}

	default S reverseConvert(T source, Class<? extends T> sourceClass, TypeDescriptor targetType) throws E {
		return reverseConvert(source, TypeDescriptor.valueOf(sourceClass), targetType);
	}

	S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}