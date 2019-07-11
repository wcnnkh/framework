package scw.core.utils;

import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import scw.core.lazy.DefaultLazyFactory;
import scw.core.lazy.LazyFactory;

public final class FormatUtils {
	private FormatUtils() {
	};

	private static final ThreadLocal<LocalFormat> LOCAL = new ThreadLocal<LocalFormat>() {
		protected LocalFormat initialValue() {
			return new LocalFormat();
		};
	};

	public static LocalFormat getLocalFormat() {
		return LOCAL.get();
	}

	public static class LocalFormat {
		private LazyFactory<String, LazyFactory<Locale, SimpleDateFormat>> simpleDateFormatMap = new DefaultLazyFactory<String, LazyFactory<Locale, SimpleDateFormat>>() {
			protected Map<String, LazyFactory<Locale, SimpleDateFormat>> initMap() {
				return new HashMap<String, LazyFactory<Locale, SimpleDateFormat>>(
						8);
			};

			@Override
			protected LazyFactory<Locale, SimpleDateFormat> createValue(
					final String pattern) {

				return new DefaultLazyFactory<Locale, SimpleDateFormat>() {

					protected Map<Locale, SimpleDateFormat> initMap() {
						return new HashMap<Locale, SimpleDateFormat>(4);
					};

					@Override
					protected SimpleDateFormat createValue(Locale locale) {
						return new SimpleDateFormat(pattern, locale);
					}
				};
			}
		};

		public SimpleDateFormat getSimpleDateFormat(String pattern,
				Locale locale) {
			return simpleDateFormatMap.get(pattern).get(locale);
		}

		public SimpleDateFormat getSimpleDateFormat(String pattern) {
			return getSimpleDateFormat(pattern,
					Locale.getDefault(Locale.Category.FORMAT));
		}

		private LazyFactory<String, DecimalFormat> decimalFormatMap = new DefaultLazyFactory<String, DecimalFormat>() {

			@Override
			protected DecimalFormat createValue(String key) {
				return new DecimalFormat(key);
			}
		};

		public DecimalFormat getDecimalFormat(String pattern) {
			return decimalFormatMap.get(pattern);
		}
	}

	private static final String DEFAULT_PLACEHOLDER = "{}";

	public static String formatPlaceholder(String text, String placeholder,
			Object... args) {
		StringBuilder sb = new StringBuilder();
		try {
			formatPlaceholder(sb, text, placeholder, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public static void formatPlaceholder(Appendable appendable, String text,
			String placeholder, Object... args) throws Exception {
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
		return getLocalFormat()
				.getDecimalFormat(new String(charBuffer.array()))
				.format(number);
	}

	public static Date getDate(String date, String formatter)
			throws ParseException {
		return getLocalFormat().getSimpleDateFormat(formatter).parse(date);
	}

	public static String dateFormat(Date date, String formatter) {
		return getLocalFormat().getSimpleDateFormat(formatter).format(date);
	}
}
