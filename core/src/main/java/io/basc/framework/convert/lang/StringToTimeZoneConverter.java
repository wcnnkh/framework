package io.basc.framework.convert.lang;

import java.util.TimeZone;

import io.basc.framework.convert.Converter;
import io.basc.framework.util.TimeUtils;

public class StringToTimeZoneConverter implements Converter<String, TimeZone> {

	public TimeZone convert(String source) {
		return TimeUtils.parseTimeZoneString(source);
	}

}
