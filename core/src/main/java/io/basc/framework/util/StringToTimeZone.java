package io.basc.framework.util;

import java.util.TimeZone;
import java.util.function.Function;

public class StringToTimeZone implements Function<String, TimeZone> {
	public static final StringToTimeZone DEFAULT = new StringToTimeZone();

	public TimeZone apply(String source) {
		return TimeUtils.parseTimeZoneString(source);
	}

}
