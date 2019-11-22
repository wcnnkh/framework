package scw.core.utils;

import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import scw.core.PropertyFactory;
import scw.core.StringFormat;

public final class FormatUtils {
	private FormatUtils() {
	};
	private static final String DEFAULT_PLACEHOLDER = "{}";

	public static String formatPlaceholder(Object text, String placeholder, Object... args) {
		StringBuilder sb = new StringBuilder();
		try {
			formatPlaceholder(sb, text, placeholder, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static void formatPlaceholder(Appendable appendable, Object format, String placeholder, Object... args)
			throws Exception {
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

	public static Date getDate(String date, String formatter) throws ParseException {
		return new SimpleDateFormat(formatter).parse(date);
	}

	public static String dateFormat(Date date, String formatter) {
		return new SimpleDateFormat(formatter).format(date);
	}

	public static String format(String text, final PropertyFactory propertyFactory, boolean supportEL) {
		String newText = text;
		if (supportEL) {
			StringFormat stringFormat = new StringFormat("${", "}") {

				public String getProperty(String key) {
					return propertyFactory.getProperty(key);
				}
			};

			newText = stringFormat.format(newText);
		}

		StringFormat stringFormat = new StringFormat("{", "}") {

			public String getProperty(String key) {
				return propertyFactory.getProperty(key);
			}
		};
		return stringFormat.format(newText);
	}

	public static String format(String text, PropertyFactory propertyFactory) {
		return format(text, propertyFactory, false);
	}

	public static String format(String text, final Map<?, ?> propertyMap, boolean supportEL) {
		return format(text, new PropertyFactory() {

			public String getProperty(String key) {
				Object value = propertyMap.get(key);
				return value == null ? null : value.toString();
			}
		}, supportEL);
	}

	public static String format(String text, final Map<?, ?> propertyMap) {
		return format(text, propertyMap, false);
	}

}
