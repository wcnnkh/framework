package run.soeasy.framework.core.convert;

import lombok.NonNull;

public interface ConvertFilter<S, T> {
	T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull TypeDescriptor targetTypeDescriptor,
			Converter<? super S, ? extends T> converter) throws ConversionException;
}
