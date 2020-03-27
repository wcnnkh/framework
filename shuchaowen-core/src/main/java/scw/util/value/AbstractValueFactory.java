package scw.util.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractValueFactory<K> implements ValueFactory<K> {

	/**
	 * 获取默认的值
	 * 
	 * @param key
	 *            可能为空
	 * @return
	 */
	protected abstract Value getDefaultValue(K key);

	public Byte getByte(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsByte();
	}

	public byte getByteValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsByteValue() : value
				.getAsByteValue();
	}

	public Short getShort(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsShort() : value
				.getAsShort();
	}

	public short getShortValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsShortValue() : value
				.getAsShortValue();
	}

	public Integer getInteger(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsInteger() : value
				.getAsInteger();
	}

	public int getIntValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsIntValue() : value
				.getAsIntValue();
	}

	public Long getLong(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsLong() : value
				.getAsLong();
	}

	public long getLongValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsLongValue() : value
				.getAsLongValue();
	}

	public Boolean getBoolean(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsBoolean() : value
				.getAsBoolean();
	}

	public boolean getBooleanValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsBooleanValue() : value
				.getAsBooleanValue();
	}

	public Float getFloat(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsFloat() : value
				.getAsFloat();
	}

	public float getFloatValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsFloatValue() : value
				.getAsFloatValue();
	}

	public Double getDouble(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsDouble() : value
				.getAsDouble();
	}

	public double getDoubleValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsDoubleValue() : value
				.getAsDoubleValue();
	}

	public char getChar(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsChar() : value
				.getAsChar();
	}

	public Character getCharacter(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsCharacter() : value
				.getAsCharacter();
	}

	public String getString(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsString() : value
				.getAsString();
	}

	public BigInteger getBigInteger(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsBigInteger() : value
				.getAsBigInteger();
	}

	public BigDecimal getBigDecimal(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsBigDecimal() : value
				.getAsBigDecimal();
	}

	public Number getNumber(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsNumber() : value
				.getAsNumber();
	}

	public Class<?> getClass(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsClass() : value
				.getAsClass();
	}

	public Enum<?> getEnum(K key, Class<?> enumType) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsEnum(enumType) : value
				.getAsEnum(enumType);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(K key, Class<? extends T> type) {
		Object value;
		if (String.class == type) {
			value = getString(key);
		} else if (int.class == type) {
			value = getIntValue(key);
		} else if (Integer.class == type) {
			value = getInteger(key);
		} else if (long.class == type) {
			value = getLongValue(key);
		} else if (Long.class == type) {
			value = getLong(key);
		} else if (float.class == type) {
			value = getFloatValue(key);
		} else if (Float.class == type) {
			value = getFloat(key);
		} else if (double.class == type) {
			value = getDoubleValue(key);
		} else if (Double.class == type) {
			value = getDouble(key);
		} else if (short.class == type) {
			value = getShortValue(key);
		} else if (Short.class == type) {
			value = getShort(key);
		} else if (boolean.class == type) {
			value = getBooleanValue(key);
		} else if (Boolean.class == type) {
			value = getBoolean(key);
		} else if (byte.class == type) {
			value = getByteValue(key);
		} else if (Byte.class == type) {
			value = getByte(key);
		} else if (char.class == type) {
			value = getChar(key);
		} else if (Character.class == type) {
			value = getCharacter(key);
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			value = getBigDecimal(key);
		} else if (BigInteger.class.isAssignableFrom(type)) {
			value = getBigInteger(key);
		} else if (Number.class.isAssignableFrom(type)) {
			value = getNumber(key);
		} else if (Class.class == type) {
			value = getClass(key);
		} else if (type.isEnum()) {
			value = getEnum(key, type);
		} else {
			value = getObjectSupport(key, type);
		}
		return (T) value;
	}

	protected Object getObjectSupport(K key, Class<?> type) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsObject(type) : value
				.getAsObject(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getObject(K key, Type type) {
		if (type instanceof Class) {
			return getObject(key, (Class) type);
		}

		return getObjectSupport(key, type);
	}

	protected Object getObjectSupport(K key, Type type) {
		Value value = get(key);
		return value == null ? getDefaultValue(key).getAsObject(type) : value
				.getAsObject(type);
	}

	public Object getValue(K key, Type type, Object defaultValue) {
		return ValueUtils.getValue(this, key, type, defaultValue);
	}

	public <T> T getValue(K key, Class<? extends T> type, T defaultValue) {
		return ValueUtils.getValue(this, key, type, defaultValue);
	}
}
