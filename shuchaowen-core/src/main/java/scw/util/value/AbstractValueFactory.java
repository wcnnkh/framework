package scw.util.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractValueFactory<K, V extends Value> implements ValueFactory<K, V> {

	public abstract V getDefaultValue();

	public Byte getByte(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsByte();
	}

	public byte getByteValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsByteValue() : value.getAsByteValue();
	}

	public Short getShort(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsShort() : value.getAsShort();
	}

	public short getShortValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsShortValue() : value.getAsShortValue();
	}

	public Integer getInteger(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsInteger() : value.getAsInteger();
	}

	public int getIntValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsIntValue() : value.getAsIntValue();
	}

	public Long getLong(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsLong() : value.getAsLong();
	}

	public long getLongValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsLongValue() : value.getAsLongValue();
	}

	public Boolean getBoolean(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsBoolean() : value.getAsBoolean();
	}

	public boolean getBooleanValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsBooleanValue() : value.getAsBooleanValue();
	}

	public Float getFloat(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsFloat() : value.getAsFloat();
	}

	public float getFloatValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsFloatValue() : value.getAsFloatValue();
	}

	public Double getDouble(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsDouble() : value.getAsDouble();
	}

	public double getDoubleValue(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsDoubleValue() : value.getAsDoubleValue();
	}

	public char getChar(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsChar() : value.getAsChar();
	}

	public Character getCharacter(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsCharacter() : value.getAsCharacter();
	}

	public String getString(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsString() : value.getAsString();
	}

	public BigInteger getBigInteger(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsBigInteger() : value.getAsBigInteger();
	}

	public BigDecimal getBigDecimal(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsBigDecimal() : value.getAsBigDecimal();
	}

	public Number getNumber(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsNumber() : value.getAsNumber();
	}

	public Class<?> getClass(K key) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsClass() : value.getAsClass();
	}

	public Enum<?> getEnum(K key, Class<?> enumType) {
		Value value = get(key);
		return value == null ? getDefaultValue().getAsEnum(enumType) : value.getAsEnum(enumType);
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
	
	protected Object getObjectSupport(K key, Class<?> type){
		Value value = get(key);
		return value == null ? getDefaultValue().getAsObject(type) : value.getAsObject(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getObject(K key, Type type) {
		if (type instanceof Class) {
			return getObject(key, (Class) type);
		}
		
		return getObjectSupport(key, type);
	}
	
	protected Object getObjectSupport(K key, Type type){
		Value value = get(key);
		return value == null ? getDefaultValue().getAsObject(type) : value.getAsObject(type);
	}
}
