package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Value;

@RequiredArgsConstructor
public class LongValue extends RationalNumber {
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
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger value = o.getAsBigInteger();
			if (value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) {
				return -1;
			}
		}
		return super.compareTo(o);
	}

	@Override
	public NumberValue add(NumberValue value) {
		if (value.canAsLong()) {
			try {
				long newValue = Math.addExact(this.value, value.longValue());
				return new LongValue(newValue);
			} catch (ArithmeticException e) {
				// 结果溢出
			}
		}
		return super.add(value);
	}

	@Override
	public NumberValue subtract(NumberValue value) {
		if (value.canAsLong()) {
			try {
				long newValue = Math.subtractExact(this.value, value.longValue());
				return new LongValue(newValue);
			} catch (ArithmeticException e) {
				// 结果溢出
			}
		}
		return super.subtract(value);
	}

	@Override
	public NumberValue multiply(NumberValue value) {
		if (value.canAsLong()) {
			try {
				long newValue = Math.multiplyExact(this.value, value.longValue());
				return new LongValue(newValue);
			} catch (ArithmeticException e) {
				// 结果溢出
			}
		}
		return super.multiply(value);
	}
}
