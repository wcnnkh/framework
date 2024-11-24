package io.basc.framework.core.convert.strings;

import java.util.TimeZone;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.TimeUtils;

public class TimeZoneConverter implements ReversibleConverter<String, TimeZone, ConversionException> {

	@Override
	public TimeZone convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return TimeUtils.parseTimeZoneString(source);
	}

	@Override
	public String reverseConvert(TimeZone source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source.getDisplayName();
	}

}
