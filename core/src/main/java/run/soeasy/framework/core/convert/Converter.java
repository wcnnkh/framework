package run.soeasy.framework.core.convert;

import lombok.NonNull;

@FunctionalInterface
public interface Converter<S, T, E extends Throwable> {
	/**
	 * 是否能直接转换，无需重写此方法
	 * 
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	default boolean canDirectlyConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		if (sourceType.isAssignableTo(targetType)) {
			return true;
		}

		if (targetType.getType() == Object.class) {
			return true;
		}
		return false;
	}

	static class UnsupportedConverter<S, T, E extends Throwable> implements Converter<S, T, E> {
		static final UnsupportedConverter<?, ?, ?> INSTANCE = new UnsupportedConverter<>();

		@Override
		public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
			return false;
		}

		@Override
		public T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
			throw new ConversionFailedException(sourceType, targetType, targetType, null);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S, T, E extends Throwable> Converter<S, T, E> unsupported() {
		return (Converter<S, T, E>) UnsupportedConverter.INSTANCE;
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

	T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}