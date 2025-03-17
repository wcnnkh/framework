package run.soeasy.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.Value;

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
			if (value.compareTo(LONG_MAX_VALUE) >= 0) {
				return -1;
			}
		}
		return super.compareTo(o);
	}

}
