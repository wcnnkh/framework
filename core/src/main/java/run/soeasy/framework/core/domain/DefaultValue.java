package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;

public class DefaultValue implements Value {

	@Override
	public BigDecimal getAsBigDecimal() {
		return null;
	}

	@Override
	public BigInteger getAsBigInteger() {
		return null;
	}

	@Override
	public boolean getAsBoolean() {
		return false;
	}

	@Override
	public byte getAsByte() {
		return 0;
	}

	@Override
	public char getAsChar() {
		return 0;
	}

	@Override
	public double getAsDouble() {
		return 0;
	}

	@Override
	public Elements<? extends Value> getAsElements() {
		return Elements.empty();
	}

	@Override
	public <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		return null;
	}

	@Override
	public float getAsFloat() {
		return 0;
	}

	@Override
	public int getAsInt() {
		return 0;
	}

	@Override
	public long getAsLong() {
		return 0;
	}

	@Override
	public NumberValue getAsNumber() {
		return null;
	}

	@Override
	public short getAsShort() {
		return 0;
	}

	@Override
	public String getAsString() {
		return null;
	}

	@Override
	public Version getAsVersion() {
		return null;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

}