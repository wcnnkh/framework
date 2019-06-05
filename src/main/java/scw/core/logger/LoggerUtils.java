package scw.core.logger;

import java.io.IOException;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;

public final class LoggerUtils {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	private static final String DEFAULT_PLACEHOLDER = "{}";

	private LoggerUtils() {
	};

	public static void loggerAppend(Appendable appendable, String text, String placeholder, Object... args)
			throws IOException {
		if (StringUtils.isEmpty(text) || ArrayUtils.isEmpty(args)) {
			appendable.append(text);
			return;
		}

		String findText = StringUtils.isEmpty(placeholder) ? DEFAULT_PLACEHOLDER : placeholder;
		int lastFind = 0;
		for (int i = 0; i < args.length; i++) {
			int index = text.indexOf(findText, lastFind);
			if (index == -1) {
				break;
			}

			appendable.append(text.substring(lastFind, index));
			Object v = args[i];
			if (v == null) {
				appendable.append("null");
			} else {
				if (v instanceof LoggerAppend) {
					((LoggerAppend) v).appendLogger(appendable);
				} else {
					appendable.append(v.toString());
				}
			}
			lastFind = index + findText.length();
		}

		if (lastFind == 0) {
			appendable.append(text);
		} else {
			appendable.append(text.substring(lastFind));
		}
	}

	public static void loggerAppend(Appendable appendable, String time, String level, String tag,
			LoggerAppend loggerAppend) throws IOException {
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

		if (loggerAppend != null) {
			if(b){
				appendable.append(" - ");
			}
			b = true;
			loggerAppend.appendLogger(appendable);
		}
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag,
			LoggerAppend loggerAppend) throws IOException {
		loggerAppend(appendable, XTime.format(time, TIME_FORMAT), level, tag, loggerAppend);
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag, String placeholder,
			String msg, Object... args) throws IOException {
		LoggerAppend loggerAppend = new DefaultLoggerFormatAppend(msg, placeholder, args);
		loggerAppend(appendable, time, level, tag, loggerAppend);
	}

	public static void info(Class<?> clazz, String msg, Object... args) {
		StringBuilder sb = new StringBuilder(256);
		try {
			loggerAppend(sb, System.currentTimeMillis(), "INFO", clazz.getName(), null, msg, args);
			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void warn(Class<?> clazz, String msg, Object... args) {
		StringBuilder sb = new StringBuilder(256);
		try {
			loggerAppend(sb, System.currentTimeMillis(), "WARN", clazz.getName(), null, msg, args);
			System.err.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
