package io.basc.framework.util;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import io.basc.framework.lang.Nullable;
import io.basc.framework.text.placeholder.PlaceholderFormat;

public final class FormatUtils {
	private static final String DEFAULT_PLACEHOLDER = "{}";

	private FormatUtils() {
	};

	public static String formatPlaceholder(Object text, String placeholder,
			Object... args) {
		StringBuilder sb = new StringBuilder();
		try {
			formatPlaceholder(sb, text, placeholder, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static void formatPlaceholder(Appendable appendable, Object format,
			@Nullable String placeholder, Object... args) throws IOException {
		String text = format == null ? null : format.toString();
		if (StringUtils.isEmpty(text) || ArrayUtils.isEmpty(args)) {
			appendable.append(text);
			return;
		}

		String findText = StringUtils.isEmpty(placeholder) ? DEFAULT_PLACEHOLDER
				: placeholder;
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
				if (v instanceof AppendTo) {
					((AppendTo) v).appendTo(appendable);
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

	public static Properties format(Properties properties,
			PlaceholderFormat placeholderFormat) {
		if(placeholderFormat == null) {
			return properties;
		}
		
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
				props.put(entry.getKey(),
						placeholderFormat.replacePlaceholders((String) value));
			} else {
				props.put(entry.getKey(), entry.getValue());
			}
		}
		return props;
	}
}
