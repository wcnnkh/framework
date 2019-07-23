package scw.core;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.exception.NotSupportException;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public class StringParse implements Parse<String>, Verification<CharSequence> {
	public static final StringParse DEFAULT = new StringParse();

	private final StringEmptyVerification verification;
	private final int numberRadix;

	public StringParse() {
		this(null, 10);
	}

	public StringParse(StringEmptyVerification verification, int numberRadix) {
		this.verification = verification;
		this.numberRadix = numberRadix;
	}

	public boolean verification(CharSequence data) {
		if (verification == null) {
			return StringEmptyVerification.INSTANCE.verification(data);
		} else {
			return verification.verification(data) || StringEmptyVerification.INSTANCE.verification(data);
		}
	}

	public final StringEmptyVerification getVerification() {
		return verification;
	}

	public final int getNumberRadix() {
		return numberRadix;
	}

	protected Object castInteger(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return null;
		}

		return Integer.parseInt(text, numberRadix);
	}

	protected Object castIntValue(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return 0;
		}

		return Integer.parseInt(text, numberRadix);
	}

	protected Object castLong(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return null;
		}
		return Long.parseLong(text, numberRadix);
	}

	protected Object castLongValue(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return 0;
		}
		return Long.parseLong(text, numberRadix);
	}

	protected Object castBoolean(String e) {
		return parseBoolean(e, this, null);
	}

	protected Object castBooleanValue(String e) {
		return parseBoolean(e, this, false);
	}

	protected Object castShort(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return null;
		}
		return Short.parseShort(text, numberRadix);
	}

	protected Object castShortValue(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return 0;
		}
		return Short.parseShort(text, numberRadix);
	}

	protected Object castFloat(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return null;
		}
		return Float.parseFloat(text);
	}

	protected Object castFloatValue(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return 0f;
		}
		return Float.parseFloat(text);
	}

	protected Object castDouble(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return null;
		}
		return Double.parseDouble(text);
	}

	protected Object castDoubleValue(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return 0;
		}
		return Double.parseDouble(text);
	}

	protected Object castByte(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return null;
		}
		return Byte.parseByte(text, numberRadix);
	}

	protected Object castByteValue(String e) {
		String text = formatNumberText(e);
		if (verification(text)) {
			return 0;
		}
		return Byte.parseByte(text, numberRadix);
	}

	protected Object castCharacter(String e) {
		if (verification(e)) {
			return null;
		}
		return e.charAt(0);
	}

	protected Object castChar(String e) {
		if (verification(e)) {
			return (char) 0;
		}
		return e.charAt(0);
	}

	protected Object castBigInteger(String e) {
		if (verification(e)) {
			return null;
		}

		return new BigInteger(e, numberRadix);
	}

	protected Object castBigDecimal(String e) {
		if (verification(e)) {
			return null;
		}

		return new BigDecimal(e);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object castEnum(Class<? extends Enum> enumType, String e) {
		if (verification(e)) {
			return null;
		}

		return Enum.valueOf(enumType, e);
	}

	protected Object cast(String e, Class<?> type) {
		if (type.isArray()) {
			return castArray(e, type.getComponentType());
		}

		throw new NotSupportException("无法将" + e + "转换为" + type.getName() + "类型");
	}

	private Object castArray(String e, Class<?> type) {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parse(String e, Class<?> type) {
		if (ClassUtils.isStringType(type)) {
			return e;
		}

		if (Integer.class == type) {
			return castInteger(e);
		}

		if (int.class == type) {
			return castIntValue(e);
		}

		if (Long.class == type) {
			return castLong(e);
		}

		if (long.class == type) {
			return castLongValue(e);
		}

		if (Boolean.class == type) {
			return castBoolean(e);
		}

		if (boolean.class == type) {
			return castBooleanValue(e);
		}

		if (Short.class == type) {
			return castShort(e);
		}

		if (short.class == type) {
			return castShortValue(e);
		}

		if (Float.class == type) {
			return castFloat(e);
		}

		if (float.class == type) {
			return castFloatValue(e);
		}

		if (Double.class == type) {
			return castDouble(e);
		}

		if (double.class == type) {
			return castDoubleValue(e);
		}

		if (Byte.class == type) {
			return castByte(e);
		}

		if (byte.class == type) {
			return castByteValue(e);
		}

		if (Character.class == type) {
			return castCharacter(e);
		}

		if (char.class == type) {
			return castChar(e);
		}

		if (BigInteger.class == type) {
			return castBigInteger(e);
		}

		if (BigDecimal.class == type) {
			return castBigDecimal(e);
		}

		if (type.isEnum()) {
			return castEnum((Class<? extends Enum>) type, e);
		}
		return cast(e, type);
	}

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

	public static Boolean parseBoolean(String text, Verification<CharSequence> verification, Boolean defaultValue) {
		if (verification.verification(text)) {
			return defaultValue;
		}

		return "1".equals(text) || "true".equalsIgnoreCase(text) || "yes".equalsIgnoreCase(text)
				|| "T".equalsIgnoreCase(text);
	}

	public static boolean parseBoolean(String text) {
		return parseBoolean(text, StringEmptyVerification.INSTANCE, false);
	}

	public static Byte parseByte(String text, Verification<CharSequence> verification, Byte defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}
		return Byte.parseByte(text);
	}

	public static short parseShortValue(String text) {
		return parseShort(text, StringEmptyVerification.INSTANCE, (short) 0);
	}

	public static Short parseShort(String text, Verification<CharSequence> verification, Short defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}
		return Short.parseShort(text);
	}

	public static Integer parseInteger(String text, Verification<CharSequence> verification, Integer defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}
		return Integer.parseInt(v);
	}

	public static int parseIntValue(String text) {
		return parseInteger(text, StringEmptyVerification.INSTANCE, 0);
	}

	public static long parseLongValue(String text) {
		return parseLong(text, StringEmptyVerification.INSTANCE, 0L);
	}

	public static byte parseByteValue(String text) {
		return parseByte(text, StringEmptyVerification.INSTANCE, (byte) 0);
	}

	public static Long parseLong(String text, Verification<CharSequence> verification, Long defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}
		return Long.parseLong(v);
	}

	public static float parseFloatValue(String text) {
		return parseFloat(text, StringEmptyVerification.INSTANCE, 0f);
	}

	public static Float parseFloat(String text, Verification<CharSequence> verification, Float defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}
		return Float.parseFloat(v);
	}

	public static double parseDoubleValue(String text) {
		return parseDouble(text, StringEmptyVerification.INSTANCE, 0d);
	}

	public static Double parseDouble(String text, Verification<CharSequence> verification, Double defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}
		return Double.parseDouble(v);
	}

	public static char parseCharValue(String text) {
		return parseChar(text, StringEmptyVerification.INSTANCE, (char) 0);
	}

	public static Character parseChar(String text, Verification<CharSequence> verification, Character defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}

		return text.charAt(0);
	}

	public static BigInteger parseBigInteger(String text, int radix, Verification<CharSequence> verification,
			BigInteger defaultValue) {
		String v = formatNumberText(text);
		if (verification.verification(v)) {
			return defaultValue;
		}

		return new BigInteger(v, radix);
	}

	public static BigDecimal parseBigDecimal(String text, Verification<CharSequence> verification,
			BigDecimal defaultValue) {
		if (verification.verification(text)) {
			return defaultValue;
		}

		return new BigDecimal(text);
	}
}
