package run.soeasy.framework.core.time;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.Converters;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class TimeFormatter extends Converters {

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		ConversionException conversionException = null;
		for (Converter<? super Object, ? extends Object> converter : this) {
			if (converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				try {
					return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
				} catch (ConversionException e) {
					if (conversionException == null) {
						conversionException = e;
					}
				}
			}
		}
		throw conversionException != null ? conversionException
				: new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
