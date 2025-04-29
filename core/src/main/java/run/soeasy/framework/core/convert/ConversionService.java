package run.soeasy.framework.core.convert;

import java.util.function.BiFunction;

import lombok.NonNull;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService
		extends Converter<Object, Object, ConversionException>, BiFunction<TypedValue, TypeDescriptor, Object> {

	@Override
	boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType);

	@Override
	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return apply(TypedValue.of(source, sourceType), targetType);
	}

	@Override
	Object apply(TypedValue value, TypeDescriptor targetType);
}
