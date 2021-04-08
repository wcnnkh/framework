package scw.util;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.lang.FormatterException;
import scw.lang.Nullable;
import scw.util.placeholder.PropertyResolver;

public final class FormatUtils {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	private static final String DEFAULT_PLACEHOLDER = "{}";

	private FormatUtils() {
	};

	public static String formatPlaceholder(Object text, String placeholder, Object... args) {
		StringBuilder sb = new StringBuilder();
		try {
			formatPlaceholder(sb, text, placeholder, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static void formatPlaceholder(Appendable appendable, Object format, @Nullable String placeholder, Object... args)
			throws IOException {
		String text = format == null ? null : format.toString();
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
				if (v instanceof StringAppend) {
					((StringAppend) v).appendTo(appendable);
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

	/**
	 * 保留小数点精度
	 * 
	 * @param number
	 * @param len
	 *            保留多少位
	 * @return
	 */
	public static String formatNumberPrecision(double number, int len) {
		if (len < 0) {
			throw new IllegalStateException("len < 0");
		}

		if (len == 0) {
			return ((long) number) + "";
		}

		if (number == 0) {
			CharBuffer charBuffer = CharBuffer.allocate(len + 2);
			charBuffer.put('0');
			charBuffer.put('.');
			for (int i = 0; i < len; i++) {
				charBuffer.put('0');
			}
			return new String(charBuffer.array());
		}

		CharBuffer charBuffer = CharBuffer.allocate(len + 3);
		charBuffer.put("#0.");
		for (int i = 0; i < len; i++) {
			charBuffer.put("0");
		}
		return new DecimalFormat(new String(charBuffer.array())).format(number);
	}

	public static Date getDate(String date, String formatter) throws FormatterException {
		try {
			return new SimpleDateFormat(formatter).parse(date);
		} catch (ParseException e) {
			throw new FormatterException("time=" + date + ", formatter=" + formatter, e);
		}
	}

	public static String dateFormat(Date date, String formatter) {
		return new SimpleDateFormat(formatter).format(date);
	}
	
	public static void loggerAppend(Appendable appendable, String time, String level, String tag,
			StringAppend stringAppend) throws IOException {
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
			StringAppend stringAppend) throws IOException {
		loggerAppend(appendable, XTime.format(time, TIME_FORMAT), level, tag, stringAppend);
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag, String placeholder,
			Object msg, Object... args) throws IOException {
		StringAppend loggerAppend = new PlaceholderFormatAppend(msg, placeholder, args);
		loggerAppend(appendable, time, level, tag, loggerAppend);
	}

	public static Properties format(Properties properties, PropertyResolver propertyResolver) {
		if (properties == null || properties.isEmpty()) {
			return properties;
		}

		Properties props = new Properties();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			if (value instanceof String) {
				props.put(entry.getKey(), propertyResolver.resolvePlaceholders((String) value));
			} else {
				props.put(entry.getKey(), entry.getValue());
			}
		}
		return props;
	}
}
