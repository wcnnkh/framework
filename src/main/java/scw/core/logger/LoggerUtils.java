package scw.core.logger;

import scw.core.utils.StringAppend;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;

public final class LoggerUtils {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

	private LoggerUtils() {
	};

	public static void loggerAppend(Appendable appendable, String time, String level, String tag,
			StringAppend stringAppend) throws Exception {
		boolean b = false;
		if (!StringUtils.isEmpty(time)) {
			appendable.append(time);
			b = true;
		}

		if (!StringUtils.isEmpty(level)) {
			if (b) {
				appendable.append(" ");
			}
			b = true;
			appendable.append(level);
		}

		if (!StringUtils.isEmpty(tag)) {
			if (b) {
				appendable.append(" ");
			}
			b = true;
			appendable.append("[");
			appendable.append(tag);
			appendable.append("]");
		}

		if (stringAppend != null) {
			if (b) {
				appendable.append(" - ");
			}
			b = true;
			stringAppend.appendTo(appendable);
		}
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag,
			StringAppend stringAppend) throws Exception {
		loggerAppend(appendable, XTime.format(time, TIME_FORMAT), level, tag, stringAppend);
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag, String placeholder,
			String msg, Object... args) throws Exception {
		StringAppend loggerAppend = new DefaultLoggerFormatAppend(msg, placeholder, args);
		loggerAppend(appendable, time, level, tag, loggerAppend);
	}

	public static void info(Class<?> clazz, String msg, Object... args) {
		StringBuilder sb = new StringBuilder(256);
		try {
			loggerAppend(sb, System.currentTimeMillis(), "INFO", clazz.getName(), null, msg, args);
			System.out.println(sb.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void warn(Class<?> clazz, String msg, Object... args) {
		StringBuilder sb = new StringBuilder(256);
		try {
			loggerAppend(sb, System.currentTimeMillis(), "WARN", clazz.getName(), null, msg, args);
			System.err.println(sb.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
