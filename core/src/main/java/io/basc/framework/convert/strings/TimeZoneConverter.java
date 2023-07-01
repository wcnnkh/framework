package io.basc.framework.convert.strings;

import java.util.TimeZone;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.TimeUtils;

public class TimeZoneConverter implements ReversibleConverter<String, TimeZone, ConversionException> {

	@Override
	public TimeZone convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return TimeUtils.parseTimeZoneString(source);
	}

	@Override
	public String invert(TimeZone source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source.getDisplayName();
	}

}
