package scw.util.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.utils.ClassUtils;

public abstract class AbstractStringValue extends AbstractValue {
	
	public AbstractStringValue(Value defaultValue) {
		super(defaultValue);
	}

	protected boolean isEmpty(String value) {
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

	public Byte getAsByte() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsByte();
		}
		return Byte.parseByte(v, getNumberRadix());
	}

	public byte getAsByteValue() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsByteValue();
		}

		return Byte.parseByte(v, getNumberRadix());
	}

	public Short getAsShort() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsShort();
		}
		return Short.parseShort(v, getNumberRadix());
	}

	public short getAsShortValue() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsShortValue();
		}
		return Short.parseShort(v, getNumberRadix());
	}

	public Integer getAsInteger() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsInteger();
		}
		return Integer.parseInt(v, getNumberRadix());
	}

	public int getAsIntValue() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsIntValue();
		}
		return Integer.parseInt(v, getNumberRadix());
	}

	public Long getAsLong() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsLong();
		}
		return Long.parseLong(v, getNumberRadix());
	}

	public long getAsLongValue() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsLongValue();
		}
		return Long.parseLong(v, getNumberRadix());
	}

	protected boolean parseBooleanValue(String value) {
		return "1".equals(value) || "true".equalsIgnoreCase(value)
				|| "yes".equalsIgnoreCase(value) || "T".equalsIgnoreCase(value);
	}

	public Boolean getAsBoolean() {
		String v = getAsString();
		if (isEmpty(v)) {
			return getDefaultValue().getAsBoolean();
		}
		return parseBooleanValue(v);
	}

	public boolean getAsBooleanValue() {
		String v = getAsString();
		if (isEmpty(v)) {
			return getDefaultValue().getAsBooleanValue();
		}
		return parseBooleanValue(v);
	}

	public Float getAsFloat() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsFloat();
		}
		return Float.parseFloat(v);
	}

	public float getAsFloatValue() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsFloatValue();
		}
		return Float.parseFloat(v);
	}

	public Double getAsDouble() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsDouble();
		}
		return Double.parseDouble(v);
	}

	public double getAsDoubleValue() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsDoubleValue();
		}
		return Double.parseDouble(v);
	}

	public char getAsChar() {
		String v = getAsString();
		if (isEmpty(v)) {
			return getDefaultValue().getAsChar();
		}
		return v.charAt(0);
	}

	public Character getAsCharacter() {
		String v = getAsString();
		if (isEmpty(v)) {
			return getDefaultValue().getAsCharacter();
		}

		return v.charAt(0);
	}

	public BigInteger getAsBigInteger() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsBigInteger();
		}
		return new BigInteger(v, getNumberRadix());
	}

	public BigDecimal getAsBigDecimal() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsBigDecimal();
		}
		return new BigDecimal(v);
	}

	public Class<?> getAsClass() {
		String v = getAsString();
		if (isEmpty(v)) {
			return getDefaultValue().getAsClass();
		}

		try {
			return ClassUtils.forName(v);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enum<?> getAsEnum(Class<?> enumType) {
		String v = getAsString();
		if (isEmpty(v)) {
			return getDefaultValue().getAsEnum(enumType);
		}
		return Enum.valueOf((Class<? extends Enum>) enumType, v);
	}

	public Number getAsNumber() {
		String v = formatNumberText(getAsString());
		if (isEmpty(v)) {
			return getDefaultValue().getAsNumber();
		}
		return new BigDecimal(v);
	}

	@Override
	public String toString() {
		return getAsString();
	}
}
