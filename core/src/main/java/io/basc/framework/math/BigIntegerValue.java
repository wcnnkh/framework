package io.basc.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerValue extends RationalNumber {
	private static final long serialVersionUID = 1L;

	public static final BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);

	public static final BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);

	private BigInteger bigInteger;

	public BigIntegerValue(BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}

	public int compareTo(NumberValue o) {
		if (o instanceof BigIntegerValue) {
			return bigInteger.compareTo(((BigIntegerValue) o).bigInteger);
		}
		return super.compareTo(o);
	}

	protected NumberValue addInternal(NumberValue value) {
		return new BigIntegerValue(bigInteger.add(value.getAsBigDecimal().toBigIntegerExact()));
	}

	protected NumberValue subtractInternal(NumberValue value) {
		return new BigIntegerValue(bigInteger.subtract(value.getAsBigDecimal().toBigIntegerExact()));
	}

	protected NumberValue multiplyInternal(NumberValue value) {
		return new BigIntegerValue(bigInteger.multiply(value.getAsBigDecimal().toBigIntegerExact()));
	}

	protected NumberValue divideInternal(NumberValue value) {
		return new BigIntegerValue(bigInteger.divide(value.getAsBigDecimal().toBigIntegerExact()));
	}

	protected NumberValue remainderInternal(NumberValue value) {
		return new BigIntegerValue(bigInteger.remainder(value.getAsBigDecimal().toBigIntegerExact()));
	}

	protected NumberValue powInternal(NumberValue value) {
		return new BigIntegerValue(bigInteger.pow(value.getAsBigDecimal().intValueExact()));
	}

	public NumberValue abs() {
		return new BigIntegerValue(bigInteger.abs());
	}

	@Override
	public int hashCode() {
		return bigInteger.hashCode();
	}

	@Override
	public String toString() {
		return bigInteger.toString();
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(bigInteger);
	}

	@Override
	public BigInteger getAsBigInteger() {
		return bigInteger;
	}

	@Override
	public String getAsString() {
		// TODO Auto-generated method stub
		return null;
	}
}
