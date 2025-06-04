package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T> {
	/**
	 * Return {@code true} if objects of {@code sourceTypeDescriptor} can be
	 * converted to the {@code targetTypeDescriptor}. The TypeDescriptors provide
	 * additional context about the source and target locations where conversion
	 * would occur, often object fields or property locations.
	 * <p>
	 * If this method returns {@code true}, it means
	 * {@link #convert(Object, TypeDescriptor, TypeDescriptor)} is capable of
	 * converting an instance of {@code sourceTypeDescriptor} to
	 * {@code targetTypeDescriptor}.
	 * <p>
	 * Special note on collections, arrays, and maps types: For conversion between
	 * collection, array, and map types, this method will return {@code true} even
	 * though a convert invocation may still generate a {@link ConversionException}
	 * if the underlying elements are not convertible. Callers are expected to
	 * handle this exceptional case when working with collections and maps.
	 * 
	 * @param sourceTypeDescriptor context about the source type to convert from
	 *                             (may be {@code null} if source is {@code null})
	 * @param targetTypeDescriptor context about the target type to convert to
	 *                             (required)
	 * @return {@code true} if a conversion can be performed between the source and
	 *         target types, {@code false} if not
	 * @throws IllegalArgumentException if {@code targetTypeDescriptor} is
	 *                                  {@code null}
	 */
	default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return true;
	}

	/**
	 * 传输
	 * 
	 * @param source
	 * @param sourceTypeDescriptor
	 * @param target
	 * @param targetTypeDescriptor
	 * @return
	 * @throws ConversionException
	 */
	boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull T target,
			@NonNull TypeDescriptor targetTypeDescriptor);
}
