package io.basc.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.util.Value;

public class BigIntegerValue extends RationalNumber {
	private static final long serialVersionUID = 1L;

	public static final BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);

	public static final BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);

	private BigInteger value;

	public BigIntegerValue(BigInteger value) {
		this.value = value;
	}

	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger value = o.getAsBigInteger();
			return this.value.compareTo(value);
		}
		return super.compareTo(o);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	@Override
	public BigInteger getAsBigInteger() {
		return value;
	}

	@Override
	public String getAsString() {
		return value.toString();
	}
}
