package scw.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractValueWrapper implements Value{
	public abstract Value getValue();

	public <T> T getAsObject(Class<? extends T> type) {
		return getValue().getAsObject(type);
	}

	public Object getAsObject(Type type) {
		return getValue().getAsObject(type);
	}

	public String getAsString() {
		return getValue().getAsString();
	}

	public Byte getAsByte() {
		return getValue().getAsByte();
	}

	public byte getAsByteValue() {
		return getValue().getAsByteValue();
	}

	public Short getAsShort() {
		return getAsShort();
	}

	public short getAsShortValue() {
		return getValue().getAsShortValue();
	}

	public Integer getAsInteger() {
		return getValue().getAsInteger();
	}

	public int getAsIntValue() {
		return getValue().getAsIntValue();
	}

	public Long getAsLong() {
		return getValue().getAsLong();
	}

	public long getAsLongValue() {
		return getValue().getAsLongValue();
	}

	public Boolean getAsBoolean() {
		return getValue().getAsBoolean();
	}

	public boolean getAsBooleanValue() {
		return getValue().getAsBooleanValue();
	}

	public Float getAsFloat() {
		return getValue().getAsFloat();
	}

	public float getAsFloatValue() {
		return getValue().getAsFloatValue();
	}

	public Double getAsDouble() {
		return getValue().getAsDouble();
	}

	public double getAsDoubleValue() {
		return getValue().getAsDoubleValue();
	}

	public char getAsChar() {
		return getValue().getAsChar();
	}

	public Character getAsCharacter() {
		return getValue().getAsCharacter();
	}

	public BigInteger getAsBigInteger() {
		return getValue().getAsBigInteger();
	}

	public BigDecimal getAsBigDecimal() {
		return getValue().getAsBigDecimal();
	}

	public Number getAsNumber() {
		return getValue().getAsNumber();
	}

	public boolean isEmpty() {
		return getValue().isEmpty();
	}

	public boolean isNumber() {
		return getValue().isNumber();
	}

	public Class<?> getAsClass() {
		return getValue().getAsClass();
	}

	public Enum<?> getAsEnum(Class<?> enumType) {
		return getValue().getAsEnum(enumType);
	}
	
	@Override
	public String toString() {
		return getValue().toString();
	}
}
