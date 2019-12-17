package scw.util;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.utils.ClassUtils;
import scw.lang.NestedRuntimeException;
import scw.lang.NotSupportException;

public abstract class AbstractValue implements Value {
	@SuppressWarnings("unchecked")
	public <T> T parseObject(Class<? extends T> type) {
		Object v = null;
		if (String.class == type) {
			v = parseString();
		} else if (int.class == type) {
			v = parseIntValue();
		} else if (Integer.class == type) {
			v = parseInteger();
		} else if (long.class == type) {
			v = parseLongValue();
		} else if (Long.class == type) {
			v = parseLong();
		} else if (float.class == type) {
			v = parseFloatValue();
		} else if (Float.class == type) {
			v = parseFloat();
		} else if (double.class == type) {
			v = parseDoubleValue();
		} else if (Double.class == type) {
			v = parseDouble();
		} else if (short.class == type) {
			v = parseShortValue();
		} else if (Short.class == type) {
			v = parseShort();
		} else if (boolean.class == type) {
			v = parseBooleanValue();
		} else if (Boolean.class == type) {
			v = parseBoolean();
		} else if (byte.class == type) {
			v = parseByteValue();
		} else if (Byte.class == type) {
			v = parseByte();
		} else if (char.class == type) {
			v = parseChar();
		} else if (Character.class == type) {
			v = parseCharacter();
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			v = parseBigDecimal();
		} else if (BigInteger.class.isAssignableFrom(type)) {
			v = parseBigInteger();
		} else if (Number.class.isAssignableFrom(type)) {
			v = parseNumber();
		} else if (Class.class == type) {
			v = parseClass();
		} else if (type.isEnum()) {
			v = parseEnum(type);
		} else {
			v = notSupportParse(type);
		}
		return (T) v;
	}

	protected <T> T notSupportParse(Class<? extends T> type) {
		throw new NotSupportException(type.toString());
	}

	protected <T> T notSupportParse(Type type) {
		throw new NotSupportException(type.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object parseObject(Type type) {
		if (type instanceof Class) {
			return parseObject((Class) type);
		}

		Object v = null;
		if (String.class == type) {
			v = parseString();
		} else if (int.class == type) {
			v = parseIntValue();
		} else if (Integer.class == type) {
			v = parseInteger();
		} else if (long.class == type) {
			v = parseLongValue();
		} else if (Long.class == type) {
			v = parseLong();
		} else if (float.class == type) {
			v = parseFloatValue();
		} else if (Float.class == type) {
			v = parseFloat();
		} else if (double.class == type) {
			v = parseDoubleValue();
		} else if (Double.class == type) {
			v = parseDouble();
		} else if (short.class == type) {
			v = parseShortValue();
		} else if (Short.class == type) {
			v = parseShort();
		} else if (boolean.class == type) {
			v = parseBooleanValue();
		} else if (Boolean.class == type) {
			v = parseBoolean();
		} else if (byte.class == type) {
			v = parseByteValue();
		} else if (Byte.class == type) {
			v = parseByte();
		} else if (char.class == type) {
			v = parseChar();
		} else if (Character.class == type) {
			v = parseCharacter();
		} else if (BigDecimal.class == type) {
			v = parseBigDecimal();
		} else if (BigInteger.class == type) {
			v = parseBigInteger();
		} else if (Number.class == type) {
			v = parseNumber();
		} else if (Class.class == type) {
			v = parseClass();
		} else {
			v = notSupportParse(type);
		}
		return v;
	}

	public boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public int getNumberRadix() {
		return 10;
	}

	/**
	 * 可以解决1,234这种问题
	 * 
	 * @param text
	 * @return
	 */
	public String formatNumberText(String value) {
		if (isEmpty(value)) {
			return value;
		}

		char[] chars = new char[value.length()];
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = value.charAt(i);
			if (c == ' ' || c == ',') {
				continue;
			}
			chars[pos++] = c;
		}
		return pos == 0 ? null : new String(chars, 0, pos);
	}

	protected Number getDefaultNumberValue() {
		return 0;
	}

	public Byte parseByte() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return Byte.parseByte(v, getNumberRadix());
	}

	public byte parseByteValue() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return getDefaultNumberValue().byteValue();
		}
		return Byte.parseByte(v, getNumberRadix());
	}

	public Short parseShort() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return Short.parseShort(v, getNumberRadix());
	}

	public short parseShortValue() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return getDefaultNumberValue().shortValue();
		}
		return Short.parseShort(v, getNumberRadix());
	}

	public Integer parseInteger() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return Integer.parseInt(v, getNumberRadix());
	}

	public int parseIntValue() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return getDefaultNumberValue().intValue();
		}
		return Integer.parseInt(v, getNumberRadix());
	}

	public Long parseLong() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return Long.parseLong(v, getNumberRadix());
	}

	public long parseLongValue() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return getDefaultNumberValue().longValue();
		}
		return Long.parseLong(v, getNumberRadix());
	}

	public boolean parseBooleanValue(String value) {
		return "1".equals(value) || "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)
				|| "T".equalsIgnoreCase(value);
	}

	public Boolean parseBoolean() {
		String v = parseString();
		if (isEmpty(v)) {
			return null;
		}
		return parseBooleanValue(v);
	}

	public boolean parseBooleanValue() {
		String v = parseString();
		if (isEmpty(v)) {
			return false;
		}

		return parseBooleanValue(v);
	}

	public Float parseFloat() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return Float.parseFloat(v);
	}

	public float parseFloatValue() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return getDefaultNumberValue().floatValue();
		}
		return Float.parseFloat(v);
	}

	public Double parseDouble() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return Double.parseDouble(v);
	}

	public double parseDoubleValue() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return getDefaultNumberValue().doubleValue();
		}
		return Double.parseDouble(v);
	}

	public char getDefaultCharValue() {
		return 0;
	}

	public char parseChar() {
		String v = parseString();
		if (isEmpty(v)) {
			return getDefaultCharValue();
		}
		return v.charAt(0);
	}

	public Character parseCharacter() {
		String v = parseString();
		if (isEmpty(v)) {
			return null;
		}

		return v.charAt(0);
	}

	public BigInteger parseBigInteger() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return new BigInteger(v, getNumberRadix());
	}

	public BigDecimal parseBigDecimal() {
		String v = formatNumberText(parseString());
		if (isEmpty(v)) {
			return null;
		}
		return new BigDecimal(v);
	}

	public Class<?> parseClass() {
		try {
			return ClassUtils.forName(parseString());
		} catch (ClassNotFoundException e) {
			throw new NestedRuntimeException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enum<?> parseEnum(Class<?> enumType) {
		String v = parseString();
		if (isEmpty(v)) {
			return null;
		}
		return Enum.valueOf((Class<? extends Enum>) enumType, v);
	}

	public Number parseNumber() {
		return parseBigDecimal();
	}
}
