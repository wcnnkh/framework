package io.basc.framework.value;

import io.basc.framework.core.ResolvableType;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public class EmptyValue implements Value, Serializable {
	private static final long serialVersionUID = 1L;
	public static final EmptyValue INSTANCE = new EmptyValue();

	public String getAsString() {
		return null;
	}

	public Byte getAsByte() {
		return null;
	}

	public byte getAsByteValue() {
		return 0;
	}

	public Short getAsShort() {
		return null;
	}

	public short getAsShortValue() {
		return 0;
	}

	public Integer getAsInteger() {
		return null;
	}

	public int getAsIntValue() {
		return 0;
	}

	public Long getAsLong() {
		return null;
	}

	public long getAsLongValue() {
		return 0;
	}

	public Boolean getAsBoolean() {
		return null;
	}

	public boolean getAsBooleanValue() {
		return false;
	}

	public Float getAsFloat() {
		return null;
	}

	public float getAsFloatValue() {
		return 0;
	}

	public Double getAsDouble() {
		return null;
	}

	public double getAsDoubleValue() {
		return 0;
	}

	public char getAsChar() {
		return 0;
	}

	public Character getAsCharacter() {
		return null;
	}

	public BigInteger getAsBigInteger() {
		return null;
	}

	public BigDecimal getAsBigDecimal() {
		return null;
	}

	public Number getAsNumber() {
		return null;
	}

	public Class<?> getAsClass() {
		return null;
	}

	public Enum<?> getAsEnum(Class<?> enumType) {
		return null;
	}

	public boolean isNumber() {
		return false;
	}

	public boolean isEmpty() {
		return true;
	}

	public Object getAsObject(ResolvableType type) {
		return null;
	}

	@Override
	public <T> T getAsObject(Class<T> type) {
		return null;
	}

	@Override
	public Object getAsObject(Type type) {
		return null;
	}

	@Override
	public String toString() {
		return getAsString();
	}
}
