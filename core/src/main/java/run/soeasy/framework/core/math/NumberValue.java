package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;

public abstract class NumberValue extends Number implements Version {
	private static final long serialVersionUID = 1L;

	public static final NumberValue MINUS_ONE = new IntValue(-1);
	public static final NumberValue ZERO = new IntValue(0);
	public static final NumberValue ONE = new IntValue(1);
	public static final NumberValue TEN = new IntValue(10);

	private static RuntimeException createTooHighException(Number number) {
		return new ArithmeticException("The value[" + number + "] is too high");
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
	public byte getAsByte() throws ArithmeticException {
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
	public char getAsChar() throws ArithmeticException {
		return (char) getAsByte();
	}

	@Override
	public double getAsDouble() throws ArithmeticException {
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
	public float getAsFloat() throws ArithmeticException {
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
	public int getAsInt() throws ArithmeticException {
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
	public long getAsLong() throws ArithmeticException {
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

	@Override
	public short getAsShort() throws ArithmeticException {
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
	public final boolean isMultiple() {
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
