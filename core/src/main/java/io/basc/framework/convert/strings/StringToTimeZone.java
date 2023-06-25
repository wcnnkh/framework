package io.basc.framework.convert.strings;

import java.util.TimeZone;
import java.util.function.Function;

import io.basc.framework.util.TimeUtils;

public class StringToTimeZone implements Function<String, TimeZone> {
	public static final StringToTimeZone DEFAULT = new StringToTimeZone();

	public TimeZone apply(String source) {
		return TimeUtils.parseTimeZoneString(source);
	}

}
