package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;
import io.basc.framework.util.StringUtils;

import java.util.TimeZone;

public class StringToTimeZoneConverter implements Converter<String, TimeZone> {

	public TimeZone convert(String source) {
		return StringUtils.parseTimeZoneString(source);
	}

}
