package run.soeasy.framework.core.convert;

import lombok.NonNull;

@FunctionalInterface
public interface Converter<S, T> {
	T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetType)
			throws ConversionException;
}