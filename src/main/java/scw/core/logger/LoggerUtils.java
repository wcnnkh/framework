package scw.core.logger;

import scw.core.utils.StringUtils;
import scw.core.utils.XTime;

public final class LoggerUtils {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	private static final String DEFAULT_PLACEHOLDER = "{}";

	private LoggerUtils() {
	};

	public static String getLogMessage(String time, String level, String tag, String placeholder, String msg,
			Object... args) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(time);

		if (!StringUtils.isEmpty(level)) {
			sb.append(" ").append(level);
		}

		if (!StringUtils.isEmpty(tag)) {
			sb.append(" [").append(tag).append("]");
		}

		sb.append(" - ");
		sb.append(StringUtils.format(msg, StringUtils.isEmpty(placeholder) ? DEFAULT_PLACEHOLDER : placeholder, args));
		return sb.toString();
	}

	public static String getLogMessage(long time, String level, String tag, String placeholder, String msg,
			Object... args) {
		return getLogMessage(XTime.format(time, TIME_FORMAT), level, tag, placeholder, msg, args);
	}

	public static void info(Class<?> clazz, String msg, Object ...args){
		System.out.println(getLogMessage(System.currentTimeMillis(), Level.INFO.name(), clazz.getName(),
				null, msg, args));
	}

	public static void warn(Class<?> clazz, String msg, Object ...args){
		System.err.println(getLogMessage(System.currentTimeMillis(), Level.WARN.name(), clazz.getName(),
				null, msg, args));
	}
}
