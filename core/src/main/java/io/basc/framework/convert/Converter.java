package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

/**
 * 和{@link Inverter}的行为相反
 * 
 * @see Inverter
 * @author wcnnkh
 *
 * @param <S>
 * @param <T>
 * @param <E>
 */
public interface Converter<S, T, E extends Throwable> {

	default <R extends T> R convert(@Nullable S source, Class<? extends R> targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	default <R extends T> R convert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			Class<? extends R> targetType) throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	default <R extends T> R convert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	default <R extends T> R convert(@Nullable S source, TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	default <R extends T> R convert(@Nullable S source, @Nullable TypeDescriptor sourceType,
			Class<? extends R> targetType) throws E {
		return convert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	/*
	 * Convert the given {@code source} to the specified {@code targetType}. The
	 * TypeDescriptors provide additional context about the source and target
	 * locations where conversion will occur, often object fields or property
	 * locations.
	 * 
	 * @param source the source object to convert (may be {@code null})
	 * 
	 * @param sourceType context about the source type to convert from (may be
	 * {@code null} if source is {@code null})
	 * 
	 * @param targetType context about the target type to convert to (required)
	 * 
	 * @return the converted object, an instance of {@link
	 * TypeDescriptor#getObjectType() targetType}
	 * 
	 * @throws ConversionException if a conversion exception occurred
	 * 
	 * @throws IllegalArgumentException if targetType is {@code null}, or {@code
	 * sourceType} is {@code null} but source is not {@code null}
	 */
	<R extends T> R convert(@Nullable S source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E;
}
