package run.soeasy.framework.core.convert.strings;

import java.util.TimeZone;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToTimeZoneConverter implements StringConverter<TimeZone> {
	public static StringToTimeZoneConverter DEFAULT = new StringToTimeZoneConverter();

	@Override
	public TimeZone from(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		TimeZone timeZone = TimeZone.getTimeZone(source);
		if ("GMT".equals(timeZone.getID()) && !source.startsWith("GMT")) {
			// We don't want that GMT fallback...
			throw new IllegalArgumentException("Invalid time zone specification '" + source + "'");
		}
		return timeZone;
	}

	@Override
	public String to(TimeZone source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source.getDisplayName();
	}

}
