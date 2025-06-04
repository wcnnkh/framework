package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.Value;

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
