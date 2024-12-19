package io.basc.framework.core.convert;

/**
 * A service interface for type conversion. This is the entry point into the
 * convert system. Call {@link #convert(Object, Class)} to perform a thread-safe
 * type conversion using this system.
 */
public interface ConversionService extends Converter<Object, Object, ConversionException> {
	@Override
	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);
}
