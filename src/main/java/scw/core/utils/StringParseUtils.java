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
		if (StringUtils.isEmpty(text)) {
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

		return "1".equals(text) || "true".equalsIgnoreCase(text) || "yes".equalsIgnoreCase(text)
				|| "T".equalsIgnoreCase(text);
	}

	public static boolean parseBoolean(String text) {
		return parseBoolean(text, false);
	}

	public static byte parseByte(String text, byte defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return Byte.parseByte(text);
	}

	public static short parseShort(String text) {
		return parseShort(text, (short) 0);
	}

	public static short parseShort(String text, short defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return Short.parseShort(text);
	}

	public static int parseInt(String text, int defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return Integer.parseInt(v);
	}

	public static int parseInt(String text) {
		return parseInt(text, 0);
	}

	public static long parseLong(String text) {
		return parseLong(text, 0L);
	}

	public static byte parseByte(String text) {
		return parseByte(text, (byte) 0);
	}

	public static long parseLong(String text, long defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return Long.parseLong(v);
	}

	public static float parseFloat(String text) {
		return parseFloat(text, 0f);
	}

	public static float parseFloat(String text, float defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return Float.parseFloat(v);
	}

	public static double parseDouble(String text) {
		return parseDouble(text, 0);
	}

	public static double parseDouble(String text, double defaultValue) {
		String v = formatNumberText(text);
		if (StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		return Double.parseDouble(v);
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
			return parseInt(value, 0);
		} else if (Integer.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseInt(value, 0);
		} else if (long.class.isAssignableFrom(basicType)) {
			return parseLong(value, 0);
		} else if (Long.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseLong(value, 0L);
		} else if (float.class.isAssignableFrom(basicType)) {
			return parseFloat(value, 0f);
		} else if (Float.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseFloat(value, 0f);
		} else if (short.class.isAssignableFrom(basicType)) {
			return parseShort(value, (short) 0);
		} else if (Short.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseShort(value, (short) 0);
		} else if (boolean.class.isAssignableFrom(basicType)) {
			return parseBoolean(value);
		} else if (Boolean.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseBoolean(value);
		} else if (byte.class.isAssignableFrom(basicType)) {
			return parseByte(value, (byte) 0);
		} else if (Byte.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : parseByte(value, (byte) 0);
		} else if (char.class.isAssignableFrom(basicType)) {
			return value.charAt(0);
		} else if (Character.class.isAssignableFrom(basicType)) {
			return StringUtils.isEmpty(value) ? null : value.charAt(0);
		} else {
			return value;
		}
	}

	public static int[] parseIntArray(String[] arr) {
		if (arr == null) {
			return null;
		}

		int[] values = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			values[i] = StringUtils.isEmpty(arr[i]) ? 0 : parseInt(arr[i]);
		}
		return values;
	}

	public static long[] parseLongArray(String[] arr) {
		if (arr == null) {
			return null;
		}

		long[] values = new long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			values[i] = StringUtils.isEmpty(arr[i]) ? 0 : parseLong(arr[i]);
		}
		return values;
	}

	public static byte[] parseByteArray(String[] arr) {
		if (arr == null) {
			return null;
		}

		byte[] values = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			values[i] = StringUtils.isEmpty(arr[i]) ? 0 : parseByte(arr[i]);
		}
		return values;
	}

	public static short[] parseShortArray(String[] arr) {
		if (arr == null) {
			return null;
		}

		short[] values = new short[arr.length];
		for (int i = 0; i < arr.length; i++) {
			values[i] = StringUtils.isEmpty(arr[i]) ? 0 : parseShort(arr[i]);
		}
		return values;
	}

	public static float[] parseFloatArray(String[] arr) {
		if (arr == null) {
			return null;
		}

		float[] values = new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			values[i] = StringUtils.isEmpty(arr[i]) ? 0 : parseFloat(arr[i]);
		}
		return values;
	}

	public static double[] parseDoubleArray(String[] arr) {
		if (arr == null) {
			return null;
		}

		double[] values = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			values[i] = StringUtils.isEmpty(arr[i]) ? 0 : parseDouble(arr[i]);
		}
		return values;
	}
}
