package scw.math;

import java.math.BigDecimal;

public class BigDecimalNumberExtend implements NumberExtend {
	private final BigDecimal bigDecimal;
	
	public BigDecimalNumberExtend(String number) {
		this(new BigDecimal(number));
	}

	public BigDecimalNumberExtend(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	public NumberExtend add(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().add(numberExtend.toBigDecimal()));
	}

	public NumberExtend subtract(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().subtract(numberExtend.toBigDecimal()));
	}

	public NumberExtend multiply(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().multiply(numberExtend.toBigDecimal()));
	}

	public NumberExtend divide(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().divide(numberExtend.toBigDecimal()));
	}

	public NumberExtend remainder(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().remainder(numberExtend.toBigDecimal()));
	}

	public NumberExtend pow(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().pow(numberExtend.toBigDecimal().intValue()));
	}

	public BigDecimal toBigDecimal() {
		return bigDecimal;
	}
	
	@Override
	public int hashCode() {
		return bigDecimal.hashCode();
	}
}
