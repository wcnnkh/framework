package run.soeasy.framework.commons.format;

import lombok.NonNull;
import run.soeasy.framework.commons.time.TimeFormat;

public class XMLDateFormat extends TimeFormat {
	public XMLDateFormat(@NonNull String pattern) {
		super(pattern);
	}

	public static XMLDateFormat MILLISECOND_WITH_TIME_ZONE = new XMLDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	public static XMLDateFormat SECOND_WITH_TIME_ZONE = new XMLDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	public static XMLDateFormat SECOND = new XMLDateFormat("yyyy-MM-dd'T'HH:mm:ss");
}
