package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.ValueAccessor;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService extends Converter<Object, Object, ConversionException> {
	
	
	@Override
	boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType);

	@Override
	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return convert(ValueAccessor.of(source, sourceType), targetType);
	}

	Object convert(@NonNull ValueAccessor accessor, @NonNull TypeDescriptor targetType) throws ConversionException;
}
