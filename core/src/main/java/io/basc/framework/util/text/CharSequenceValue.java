package io.basc.framework.util.text;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.Any;

public interface CharSequenceValue extends CharSequence, Any {
	@Override
	default char charAt(int index) {
		return getAsCharSequence().charAt(index);
	}

	@Override
	default BigDecimal getAsBigDecimal() {
		return new BigDecimal(getAsString());
	}

	@Override
	default BigInteger getAsBigInteger() {
		return new BigInteger(getAsString());
	}

	@Override
	default boolean getAsBoolean() {
		String value = getAsString();
		return value == null ? false : Boolean.parseBoolean(value);
	}

	@Override
	default byte getAsByte() {
		String value = getAsString();
		return value == null ? 0 : Byte.parseByte(value);
	}

	@Override
	default char getAsChar() {
		return charAt(0);
	}

	@Override
	default double getAsDouble() {
		String value = getAsString();
		return value == null ? 0 : Double.parseDouble(value);
	}

	@Override
	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) throws IllegalArgumentException, NullPointerException {
		String value = getAsString();
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return Enum.valueOf(enumType, value);
	}

	@Override
	default CharSequence subSequence(int start, int end) {
		return getAsCharSequence().subSequence(start, end);
	}

	@Override
	default float getAsFloat() {
		String value = getAsString();
		return value == null ? 0 : Float.parseFloat(value);
	}

	@Override
	default int getAsInt() {
		String value = getAsString();
		return value == null ? 0 : Integer.parseInt(value);
	}

	@Override
	default long getAsLong() {
		String value = getAsString();
		return value == null ? 0 : Long.parseLong(value);
	}

	@Override
	default short getAsShort() {
		String value = getAsString();
		return value == null ? 0 : Short.parseShort(value);
	}

	@Override
	default Number getAsNumber() throws NumberFormatException {
		return new BigDecimal(getAsString());
	}

	@Override
	default boolean isNumber() {
		try {
			getAsNumber();
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
