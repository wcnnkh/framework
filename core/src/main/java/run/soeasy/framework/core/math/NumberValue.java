package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import run.soeasy.framework.core.Value;
import run.soeasy.framework.core.Version;
import run.soeasy.framework.core.collection.Elements;

public abstract class NumberValue extends Number implements Version {
	private static final long serialVersionUID = 1L;

	public static final NumberValue MINUS_ONE = new IntValue(-1);
	public static final NumberValue ZERO = new IntValue(0);
	public static final NumberValue ONE = new IntValue(1);
	public static final NumberValue TEN = new IntValue(10);

	private static RuntimeException createTooHighException(Number number) {
		return new IllegalStateException("The value[" + number + "] is too high");
	}

	/**
	 * 绝对值
	 * 
	 * @return
	 */
	public abstract NumberValue abs();

	/**
	 * 加法
	 * 
	 * @param value
	 * @return
	 */
	public abstract NumberValue add(NumberValue value);

	public boolean canAsByte() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) <= 0;
	}

	public boolean canAsDouble() {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) <= 0;
	}

	public boolean canAsFloat() {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) <= 0;
	}

	public boolean canAsInt() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
	}

	public boolean canAsLong() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0;
	}

	public boolean canAsShort() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) <= 0;
	}

	/**
	 * 除法
	 * 
	 * @param value
	 * @return
	 */
	public abstract NumberValue divide(NumberValue value);

	@Override
	public double doubleValue() {
		return getAsDouble();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof NumberValue) {
			return compareTo((NumberValue) obj) == 0;
		}
		return false;
	}

	@Override
	public float floatValue() {
		return getAsFloat();
	}

	@Override
	public abstract BigDecimal getAsBigDecimal();

	@Override
	public abstract BigInteger getAsBigInteger();

	@Override
	public boolean getAsBoolean() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return false;
		}

		return number.compareTo(BigInteger.ONE) == 0;
	}

	@Override
	public byte getAsByte() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.byteValue();
	}

	@Override
	public char getAsChar() {
		return (char) getAsByte();
	}

	@Override
	public double getAsDouble() {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.doubleValue();
	}

	@Override
	public Elements<? extends Value> getAsElements() {
		return Elements.singleton(this);
	}

	@Override
	public <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
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
	}

	@Override
	public float getAsFloat() {
		BigDecimal number = getAsBigDecimal();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.floatValue();
	}

	@Override
	public int getAsInt() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.intValue();
	}

	@Override
	public long getAsLong() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.longValue();
	}

	@Override
	public NumberValue getAsNumber() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
		if (NumberValue.class == requiredType) {
			return (T) this;
		}
		return Version.super.getAsObject(requiredType, defaultSupplier);
	}

	@Override
	public short getAsShort() {
		BigInteger number = getAsBigInteger();
		if (number == null) {
			return 0;
		}

		if (number.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0) {
			throw createTooHighException(number);
		}
		return number.shortValue();
	}

	@Override
	public int hashCode() {
		return getAsInt();
	}

	@Override
	public int intValue() {
		return getAsInt();
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public long longValue() {
		return getAsLong();
	}

	/**
	 * 乘法
	 * 
	 * @param value
	 * @return
	 */
	public abstract NumberValue multiply(NumberValue value);

	/**
	 * 指数运算
	 * 
	 * @param value
	 * @return
	 */
	public abstract NumberValue pow(NumberValue value);

	/**
	 * 余数
	 * 
	 * @param value
	 * @return
	 */
	public abstract NumberValue remainder(NumberValue value);

	/**
	 * 减法
	 * 
	 * @param value
	 * @return
	 */
	public abstract NumberValue subtract(NumberValue value);

	@Override
	public String toString() {
		return getAsString();
	}
}
