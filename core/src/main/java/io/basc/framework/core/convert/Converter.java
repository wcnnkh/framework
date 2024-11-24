package io.basc.framework.core.convert;

@FunctionalInterface
public interface Converter<S, T, E extends Throwable> {
	/**
	 * 是否能直接转换，无需重写此方法
	 * 
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	default boolean canDirectlyConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType != null && targetType != null && targetType.isAssignableTo(targetType)) {
			return true;
		}

		if (targetType.getType() == Object.class) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <S, T, E extends Throwable> Converter<S, T, E> unsupported() {
		return (Converter<S, T, E>) UnsupportedConverter.INSTANCE;
	}

	default boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return canConvert(sourceType, TypeDescriptor.valueOf(targetType));
	}

	default boolean canConvert(Class<?> sourceType, TypeDescriptor targetType) {
		return canConvert(sourceType == null ? null : TypeDescriptor.valueOf(sourceType), targetType);
	}

	default boolean canConvert(TypeDescriptor sourceType, Class<?> targetType) {
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
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
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
	default <R extends T> R convert(S source, Class<R> targetType) throws E {
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
	default <R extends T> R convert(S source, Class<? extends S> sourceType, Class<R> targetType) throws E {
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
	default T convert(S source, Class<? extends S> sourceType, TypeDescriptor targetType) throws E {
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
	default T convert(S source, TypeDescriptor targetType) throws E {
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
	default <R extends T> R convert(S source, TypeDescriptor sourceType, Class<R> targetType) throws E {
		return (R) convert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}