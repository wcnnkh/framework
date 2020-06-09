package scw.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class BigDecimalNumberExtend implements NumberExtend {
	private static Logger logger = LoggerUtils.getLogger(BigDecimalNumberExtend.class);
	
	private final BigDecimal bigDecimal;
	private final int scale;
	private final RoundingMode roundingMode;

	public BigDecimalNumberExtend(String number) {
		this(new BigDecimal(number), 32, RoundingMode.HALF_EVEN);
	}

	public BigDecimalNumberExtend(String number, int scale, RoundingMode roundingMode) {
		this(new BigDecimal(number), scale, roundingMode);
	}

	public BigDecimalNumberExtend(BigDecimal bigDecimal) {
		this(bigDecimal, 32, RoundingMode.HALF_EVEN);
	}

	public BigDecimalNumberExtend(BigDecimal bigDecimal, int scale, RoundingMode roundingMode) {
		this.bigDecimal = bigDecimal;
		this.roundingMode = roundingMode;
		this.scale = scale;
	}

	public NumberExtend add(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().add(numberExtend.toBigDecimal()), scale, roundingMode);
	}

	public NumberExtend subtract(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().subtract(numberExtend.toBigDecimal()), scale, roundingMode);
	}

	public NumberExtend multiply(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().multiply(numberExtend.toBigDecimal()), scale, roundingMode);
	}

	public NumberExtend divide(NumberExtend numberExtend) {
		BigDecimal bigDecimal;
		try {
			bigDecimal = toBigDecimal().divide(numberExtend.toBigDecimal());
		} catch (ArithmeticException e) {
			logger.error("{}/{} error:{}", toBigDecimal(), numberExtend.toBigDecimal(), e.getMessage());
			bigDecimal = toBigDecimal().divide(numberExtend.toBigDecimal(), scale, roundingMode);
		}
		return new BigDecimalNumberExtend(bigDecimal, scale, roundingMode);
	}

	public NumberExtend divide(NumberExtend numberExtend, int scale, RoundingMode roundingMode) {
		return new BigDecimalNumberExtend(toBigDecimal().divide(numberExtend.toBigDecimal(), scale, roundingMode),
				scale, roundingMode);
	}

	public NumberExtend remainder(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().remainder(numberExtend.toBigDecimal()), scale, roundingMode);
	}

	public NumberExtend pow(NumberExtend numberExtend) {
		return new BigDecimalNumberExtend(toBigDecimal().pow(numberExtend.toBigDecimal().intValue()), scale,
				roundingMode);
	}

	public BigDecimal toBigDecimal() {
		return bigDecimal;
	}

	@Override
	public String toString() {
		return bigDecimal.toString();
	}
}
