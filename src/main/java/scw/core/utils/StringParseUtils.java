package scw.core.utils;

public final class StringParseUtils {
	private StringParseUtils() {
	};

	/**
	 * 可以解决1,234这种问题
	 * 
	 * @param text
	 * @return
	 */
	public static String formatNumberText(String text) {
		if (text == null || text.length() == 0) {
			return text;
		}

		char[] chars = new char[text.length()];
		int pos = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ' || c == ',') {
				continue;
			}
			chars[pos++] = c;
		}
		return pos == 0 ? null : new String(chars, 0, pos);
	}

	public static boolean parseBoolean(String text, boolean defaultValue) {
		if (StringUtils.isEmpty(text)) {
			return defaultValue;
		}

		return "1".equals(text) || "true".equalsIgnoreCase(text)
				|| "yes".equalsIgnoreCase(text) || "T".equalsIgnoreCase(text);
	}

	public static boolean parseBoolean(String text) {
		return parseBoolean(text, false);
	}

	public static int parseInt(String text, int defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(text)) {
			return defaultValue;
		}
		return Integer.parseInt(v);
	}

	public static int parseInt(String text) {
		return parseInt(text, 0);
	}
	
	/**
	 * 把unicode 转成中文
	 * 
	 * @return
	 */
	public static String convertUnicode(String ori) {
		char aChar;
		int len = ori.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = ori.charAt(x++);
			if (aChar == '\\') {
				aChar = ori.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = ori.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);

		}
		return outBuffer.toString();
	}
	
	/**
	 * 如果是string类类型就返回本身
	 * 
	 * @param value
	 * @param basicType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T conversion(String value, Class<T> basicType) {
		if (ClassUtils.isStringType(basicType)) {
			return (T) value;
		} else {
			return (T) conversionBasicType(value, basicType);
		}
	}

	/**
	 * 自动把string转化为基本数据类型 string不是基本数据类型
	 * 
	 * @param value
	 * @param basicType
	 * @return
	 */
	public static Object conversionBasicType(String value, Class<?> basicType) {
		if (int.class.isAssignableFrom(basicType)) {
			return Integer.parseInt(value);
		} else if (Integer.class.isAssignableFrom(basicType)) {
			return Integer.valueOf(value);
		} else if (long.class.isAssignableFrom(basicType)) {
			return Long.parseLong(value);
		} else if (Long.class.isAssignableFrom(basicType)) {
			return Long.valueOf(value);
		} else if (float.class.isAssignableFrom(basicType)) {
			return Float.parseFloat(value);
		} else if (Float.class.isAssignableFrom(basicType)) {
			return Float.valueOf(value);
		} else if (short.class.isAssignableFrom(basicType)) {
			return Short.parseShort(value);
		} else if (Short.class.isAssignableFrom(basicType)) {
			return Short.valueOf(value);
		} else if (boolean.class.isAssignableFrom(basicType)) {
			return parseBoolean(value);
		} else if (Boolean.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseBoolean(value);
		} else if (byte.class.isAssignableFrom(basicType)) {
			return Byte.parseByte(value);
		} else if (Byte.class.isAssignableFrom(basicType)) {
			return Byte.valueOf(value);
		} else if (char.class.isAssignableFrom(basicType)) {
			return value.charAt(0);
		} else if (Character.class.isAssignableFrom(basicType)) {
			return value == null ? null : value.charAt(0);
		} else {
			return value;
		}
	}
}
