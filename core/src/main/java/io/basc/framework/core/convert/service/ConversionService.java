package io.basc.framework.core.convert.service;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TypeDescriptor;
import lombok.NonNull;

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
		return convert(Source.of(source, sourceType), targetType);
	}

	Object convert(@NonNull Source source, @NonNull TypeDescriptor targetType) throws ConversionException;
}
