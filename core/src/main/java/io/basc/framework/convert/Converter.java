package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

@FunctionalInterface
public interface Converter<S, T, E extends Throwable> {
	default boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
		return canConvert(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canConvert(@Nullable Class<?> sourceType, TypeDescriptor targetType) {
		return canConvert(sourceType == null ? null : TypeDescriptor.valueOf(sourceType), targetType);
	}

	default boolean canConvert(@Nullable TypeDescriptor sourceType, Class<?> targetType) {
		return canConvert(sourceType, TypeDescriptor.valueOf(targetType));
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
	default boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, Class)}
	 * 
	 * @see #convert(Object, TypeDescriptor, Class)
	 * @param <R>
	 * @param source
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default <R extends T> R convert(@Nullable S source, Class<R> targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, Class)}
	 * 
	 * @see #convert(Object, TypeDescriptor, Class)
	 * @param <R>
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default <R extends T> R convert(@Nullable S source, @Nullable Class<? extends S> sourceType, Class<R> targetType)
			throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 
	 * @see #convert(Object, TypeDescriptor, TypeDescriptor)
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default T convert(@Nullable S source, @Nullable Class<? extends S> sourceType, TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 
	 * @see #convert(Object, TypeDescriptor, TypeDescriptor)
	 * @param source
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default T convert(@Nullable S source, TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 
	 * @see #convert(Object, TypeDescriptor, TypeDescriptor)
	 * @param <R>
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @throws E
	 */
	@SuppressWarnings("unchecked")
	default <R extends T> R convert(@Nullable S source, @Nullable TypeDescriptor sourceType, Class<R> targetType)
			throws E {
		return (R) convert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	T convert(@Nullable S source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}