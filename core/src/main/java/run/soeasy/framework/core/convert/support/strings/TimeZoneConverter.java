package run.soeasy.framework.core.convert.support.strings;

import java.util.TimeZone;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.TimeUtils;

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
