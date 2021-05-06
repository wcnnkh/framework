package scw.convert.lang;

import java.util.TimeZone;

import scw.convert.Converter;
import scw.core.utils.StringUtils;

public class StringToTimeZoneConverter implements Converter<String, TimeZone> {

	public TimeZone convert(String source) {
		return StringUtils.parseTimeZoneString(source);
	}

}
