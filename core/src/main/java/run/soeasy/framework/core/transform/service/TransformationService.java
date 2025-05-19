package run.soeasy.framework.core.transform.service;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.Transformer;

public interface TransformationService extends Transformer<Object, Object> {

	default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return canTransform(TypeDescriptor.valueOf(sourceClass), TypeDescriptor.valueOf(targetClass));
	}

	default boolean canTransform(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetType) {
		return canTransform(TypeDescriptor.valueOf(sourceClass), targetType);
	}

	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull Class<?> targetClass) {
		return canTransform(sourceType, TypeDescriptor.valueOf(targetClass));
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
	boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType);

	default boolean transform(@NonNull Object source, @NonNull Object target) throws ConversionException {
		return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default boolean transform(@NonNull Object source, @NonNull Object target, @NonNull TypeDescriptor targetType)
			throws ConversionException {
		return transform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default <T> boolean transform(@NonNull Object source, @NonNull T target, @NonNull Class<? extends T> targetClass)
			throws ConversionException {
		return transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.valueOf(targetClass));
	}

	default boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceType, @NonNull Object target)
			throws ConversionException {
		return transform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default <T> boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return transform(source, sourceType, target, TypeDescriptor.valueOf(targetClass));
	}

	default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target)
			throws ConversionException {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.forObject(target));
	}

	default <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull Object target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, targetType);
	}

	default <S, T> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass, @NonNull T target,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return transform(source, TypeDescriptor.valueOf(sourceClass), target, TypeDescriptor.valueOf(targetClass));
	}
}
