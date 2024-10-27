package io.basc.framework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {
	static final BigInteger BYTE_MAX_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);
	static final BigDecimal DOUBLE_MAX_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);
	static final BigDecimal FLOAT_MAX_VALUE = BigDecimal.valueOf(Float.MAX_VALUE);
	static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
	static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
	static final BigInteger SHORT_MAX_VALUE = BigInteger.valueOf(Short.MAX_VALUE);

	BigDecimal getAsBigDecimal();

	BigInteger getAsBigInteger();

	@Override
	default boolean getAsBoolean() {
		if (isNumber()) {
			BigInteger number = getAsBigInteger();
			if (number == null) {
				return false;
			}

			return number.compareTo(BigInteger.ONE) == 0;
		} else {
			String value = getAsString();
			return Boolean.parseBoolean(value);
		}
	}

	default byte getAsByte() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BYTE_MAX_VALUE) > 0) {
			throw new IllegalAccessError("The value[" + number + "] is too high");
		}
		return number.byteValue();
	}

	default char getAsChar() {
		if (isNumber()) {
			return (char) getAsByte();
		} else {
			String value = getAsString();
			return value.charAt(0);
		}
	}

	@Override
	default double getAsDouble() {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(DOUBLE_MAX_VALUE) > 0) {
			throw new IllegalAccessError("The value[" + number + "] is too high");
		}
		return number.doubleValue();
	}

	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		if (isNumber()) {
			BigInteger value = getAsBigInteger();
			if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				// 不可能比int还大
				throw new IndexOutOfBoundsException(
						"The ordinal[" + value + "] of enumeration cannot be greater than " + Integer.MAX_VALUE);
			}

			int ordinal = value.intValue();
			EnumSet<T> enumSet = EnumSet.noneOf(enumType);
			for (T e : enumSet) {
				if (e.ordinal() == ordinal) {
					return e;
				}
			}
			throw new NoSuchElementException(enumType + "[" + ordinal + "]");
		} else {
			return Enum.valueOf(enumType, getAsString());
		}
	}

	default float getAsFloat() {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(FLOAT_MAX_VALUE) > 0) {
			throw new IllegalAccessError("The value[" + number + "] is too high");
		}
		return number.floatValue();
	}

	@Override
	default int getAsInt() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(INTEGER_MAX_VALUE) > 0) {
			throw new IllegalAccessError("The value[" + number + "] is too high");
		}
		return number.intValue();
	}

	@Override
	default long getAsLong() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(LONG_MAX_VALUE) > 0) {
			throw new IllegalAccessError("The value[" + number + "] is too high");
		}
		return number.longValue();
	}

	Number getAsNumber();

	@Nullable
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
		Object v = null;
		if (String.class == requiredType) {
			v = getAsString();
		} else if (ClassUtils.isInt(requiredType)) {
			v = getAsInt();
		} else if (ClassUtils.isLong(requiredType)) {
			v = getAsLong();
		} else if (ClassUtils.isFloat(requiredType)) {
			v = getAsFloat();
		} else if (ClassUtils.isDouble(requiredType)) {
			v = getAsDouble();
		} else if (ClassUtils.isShort(requiredType)) {
			v = getAsShort();
		} else if (ClassUtils.isBoolean(requiredType)) {
			v = getAsBoolean();
		} else if (ClassUtils.isByte(requiredType)) {
			v = getAsByte();
		} else if (ClassUtils.isChar(requiredType)) {
			v = getAsChar();
		} else if (BigDecimal.class == requiredType) {
			v = getAsBigDecimal();
		} else if (BigInteger.class == requiredType) {
			v = getAsBigInteger();
		} else if (Number.class == requiredType) {
			v = getAsNumber();
		} else if (requiredType.isEnum()) {
			v = getAsEnum((Class<? extends Enum>) requiredType);
		} else if (requiredType == Value.class) {
			v = this;
		} else {
			v = defaultSupplier.get();
		}
		return (T) v;
	}

	default short getAsShort() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(INTEGER_MAX_VALUE) > 0) {
			throw new IllegalAccessError("The value[" + number + "] is too high");
		}
		return number.shortValue();
	}

	String getAsString();

	boolean isNumber();
}
