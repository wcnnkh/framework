package io.basc.framework.convert.lang;

import java.util.TimeZone;
import java.util.function.Function;

import io.basc.framework.util.TimeUtils;

public class StringToTimeZoneConverter implements Function<String, TimeZone> {

	public TimeZone apply(String source) {
		return TimeUtils.parseTimeZoneString(source);
	}

}
