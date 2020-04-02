package scw.util.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.utils.ClassUtils;
import scw.lang.NestedRuntimeException;
import scw.lang.NotSupportException;

public abstract class AbstractValue implements Value {
	private final Value defaultValue;
	
	public AbstractValue(Value defaultValue){
		this.defaultValue = defaultValue;
	}
	
	protected Value getDefaultValue(){
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAsObject(Class<? extends T> type) {
		Object v = null;
		if (String.class == type) {
			v = getAsString();
		} else if (int.class == type) {
			v = getAsIntValue();
		} else if (Integer.class == type) {
			v = getAsInteger();
		} else if (long.class == type) {
			v = getAsLongValue();
		} else if (Long.class == type) {
			v = getAsLong();
		} else if (float.class == type) {
			v = getAsFloatValue();
		} else if (Float.class == type) {
			v = getAsFloat();
		} else if (double.class == type) {
			v = getAsDoubleValue();
		} else if (Double.class == type) {
			v = getAsDouble();
		} else if (short.class == type) {
			v = getAsShortValue();
		} else if (Short.class == type) {
			v = getAsShort();
		} else if (boolean.class == type) {
			v = getAsBooleanValue();
		} else if (Boolean.class == type) {
			v = getAsBoolean();
		} else if (byte.class == type) {
			v = getAsByteValue();
		} else if (Byte.class == type) {
			v = getAsByte();
		} else if (char.class == type) {
			v = getAsChar();
		} else if (Character.class == type) {
			v = getAsCharacter();
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			v = getAsBigDecimal();
		} else if (BigInteger.class.isAssignableFrom(type)) {
			v = getAsBigInteger();
		} else if (Number.class.isAssignableFrom(type)) {
			v = getAsNumber();
		} else if (Class.class == type) {
			v = getAsClass();
		} else if (type.isEnum()) {
			v = getAsEnum(type);
		} else if(type == Value.class){
			v = this;
		} else {
			v = getAsObjectNotSupport(type);
		}
		return (T) (v == null ? getDefaultValue().getAsObject(type) : v);
	}

	protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
		throw new NotSupportException(type.toString());
	}

	protected <T> T getAsObjectNotSupport(Type type) {
		throw new NotSupportException(type.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class) type);
		}

		Object v = getAsObjectNotSupport(type);
		return v == null ? getDefaultValue().getAsObject(type) : v;
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
		return "1".equals(value) || "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)
				|| "T".equalsIgnoreCase(value);
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
			throw new NestedRuntimeException(e);
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
