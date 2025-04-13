package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Value;

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
	public NumberValue add(NumberValue value) {
		if (value.canAsInt()) {
			try {
				int newValue = Math.addExact(this.value, value.intValue());
				return new IntValue(newValue);
			} catch (ArithmeticException e) {
				// 结果溢出
			}
		}
		return super.add(value);
	}

	@Override
	public NumberValue subtract(NumberValue value) {
		if (value.canAsInt()) {
			try {
				int newValue = Math.subtractExact(this.value, value.intValue());
				return new IntValue(newValue);
			} catch (ArithmeticException e) {
				// 结果溢出
			}
		}
		return super.subtract(value);
	}

	@Override
	public NumberValue multiply(NumberValue value) {
		if (value.canAsInt()) {
			try {
				int newValue = Math.multiplyExact(this.value, value.intValue());
				return new IntValue(newValue);
			} catch (ArithmeticException e) {
				// 结果溢出
			}
		}
		return super.multiply(value);
	}

	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger value = o.getAsBigInteger();
			if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
				return -1;
			}

			return Integer.compare(this.value, value.intValue());
		}
		return super.compareTo(o);
	}

	@Override
	public String getAsString() {
		return Integer.toString(value);
	}
}
