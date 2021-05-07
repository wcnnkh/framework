package scw.value;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.ResolvableType;
import scw.core.utils.ObjectUtils;

public class ValueWrapper<V extends Value> implements Value, Serializable {
	private static final long serialVersionUID = 1L;
	protected final V targetValue;

	public ValueWrapper(V targetValue) {
		this.targetValue = targetValue;
	}

	public V getTargetValue() {
		return targetValue;
	}

	public Object getAsObject(ResolvableType type) {
		return targetValue.getAsObject(type);
	}

	public <T> T getAsObject(Class<T> type) {
		return targetValue.getAsObject(type);
	}

	public Object getAsObject(Type type) {
		return targetValue.getAsObject(type);
	}

	public String getAsString() {
		return targetValue.getAsString();
	}

	public Byte getAsByte() {
		return targetValue.getAsByte();
	}

	public byte getAsByteValue() {
		return targetValue.getAsByteValue();
	}

	public Short getAsShort() {
		return getAsShort();
	}

	public short getAsShortValue() {
		return targetValue.getAsShortValue();
	}

	public Integer getAsInteger() {
		return targetValue.getAsInteger();
	}

	public int getAsIntValue() {
		return targetValue.getAsIntValue();
	}

	public Long getAsLong() {
		return targetValue.getAsLong();
	}

	public long getAsLongValue() {
		return targetValue.getAsLongValue();
	}

	public Boolean getAsBoolean() {
		return targetValue.getAsBoolean();
	}

	public boolean getAsBooleanValue() {
		return targetValue.getAsBooleanValue();
	}

	public Float getAsFloat() {
		return targetValue.getAsFloat();
	}

	public float getAsFloatValue() {
		return targetValue.getAsFloatValue();
	}

	public Double getAsDouble() {
		return targetValue.getAsDouble();
	}

	public double getAsDoubleValue() {
		return targetValue.getAsDoubleValue();
	}

	public char getAsChar() {
		return targetValue.getAsChar();
	}

	public Character getAsCharacter() {
		return targetValue.getAsCharacter();
	}

	public BigInteger getAsBigInteger() {
		return targetValue.getAsBigInteger();
	}

	public BigDecimal getAsBigDecimal() {
		return targetValue.getAsBigDecimal();
	}

	public Number getAsNumber() {
		return targetValue.getAsNumber();
	}

	public boolean isEmpty() {
		return targetValue.isEmpty();
	}

	public boolean isNumber() {
		return targetValue.isNumber();
	}

	public Class<?> getAsClass() {
		return targetValue.getAsClass();
	}

	public Enum<?> getAsEnum(Class<?> enumType) {
		return targetValue.getAsEnum(enumType);
	}

	@Override
	public String toString() {
		return targetValue.toString();
	}

	@Override
	public int hashCode() {
		return targetValue.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ValueWrapper) {
			return ObjectUtils.nullSafeEquals(((ValueWrapper) obj).targetValue,
					targetValue);
		}
		return ObjectUtils.nullSafeEquals(obj, targetValue);
	}
}
