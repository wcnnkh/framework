package io.basc.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LongValue extends NumberValue {
	private static final long serialVersionUID = 1L;
	private final long value;

	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	@Override
	public BigInteger getAsBigInteger() {
		return new BigInteger(value + "");
	}

	@Override
	public String getAsString() {
		return Long.toString(value);
	}

	@Override
	public int compareTo(NumberValue o) {
		BigInteger value = o.getAsBigInteger();
		if (value.compareTo(LONG_MAX_VALUE) >= 0) {
			return -1;
		}

		return Long.compare(this.value, value.longValue());
	}

	@Override
	public NumberValue add(NumberValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberValue subtract(NumberValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberValue multiply(NumberValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberValue divide(NumberValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberValue remainder(NumberValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberValue pow(NumberValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberValue abs() {
		// TODO Auto-generated method stub
		return null;
	}
}
