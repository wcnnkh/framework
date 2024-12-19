package io.basc.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntValue extends RationalNumber {
	private static final long serialVersionUID = 1L;
	private final int value;

	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	@Override
	public BigInteger getAsBigInteger() {
		return new BigInteger("" + value);
	}

	@Override
	public int getAsInt() {
		return value;
	}

	@Override
	public int compareTo(NumberValue o) {
		BigInteger value = o.getAsBigInteger();
		if (value.compareTo(INTEGER_MAX_VALUE) >= 0) {
			return -1;
		}

		return Integer.compare(this.value, value.intValue());
	}

	@Override
	public CharSequence getAsCharSequence() {
		return Integer.toString(value);
	}
}
