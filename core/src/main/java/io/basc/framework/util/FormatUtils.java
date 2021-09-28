package io.basc.framework.util;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.Properties;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.placeholder.PropertyResolver;

public final class FormatUtils {
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

	public static void formatPlaceholder(Appendable appendable, Object format, @Nullable String placeholder,
			Object... args) throws IOException {
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
	 * @param len    保留多少位
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
	
	/**
	 * 1M = 1024K
	 * 
	 * @param size
	 * @param toSuffix
	 * @return
	 */
	public static double parseDiskSize(String size, String toSuffix) {
		int len = size.length();
		double oldSize;
		if (size.endsWith("GB") || size.endsWith("G")) {
			oldSize = Double.parseDouble(size.substring(0, len - 2)) * 1024 * 1024 * 1024;
		} else if (size.endsWith("MB") || size.endsWith("M")) {
			oldSize = Double.parseDouble(size.substring(0, len - 2)) * 1024 * 1024;
		} else if (size.endsWith("KB") || size.endsWith("K")) {
			oldSize = Double.parseDouble(size.substring(0, len - 2)) * 1024;
		} else if (size.endsWith("B")) {
			oldSize = Double.parseDouble(size.substring(0, len - 1));
		} else {
			oldSize = Double.parseDouble(size);
		}

		if ("GB".equals(toSuffix) || "G".equals(toSuffix)) {
			return oldSize / (1024 * 1024 * 1024);
		} else if ("MB".equals(toSuffix) || "M".equals(toSuffix)) {
			return oldSize / (1024 * 1024);
		} else if ("KB".equals(toSuffix) || "K".equals(toSuffix)) {
			return oldSize / (1024);
		} else if ("B".equals(toSuffix)) {
			return oldSize;
		} else {
			return oldSize;
		}
	}
}
