package scw.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class BigDecimalHolder extends AbstractNumberHolder {
	private static Logger logger = LoggerUtils.getLogger(BigDecimalHolder.class);
	
	private final BigDecimal bigDecimal;
	private final int scale;
	private final RoundingMode roundingMode;

	public BigDecimalHolder(String number) {
		this(new BigDecimal(number), 32, RoundingMode.HALF_EVEN);
	}

	public BigDecimalHolder(String number, int scale, RoundingMode roundingMode) {
		this(new BigDecimal(number), scale, roundingMode);
	}

	public BigDecimalHolder(BigDecimal bigDecimal) {
		this(bigDecimal, 32, RoundingMode.HALF_EVEN);
	}

	public BigDecimalHolder(BigDecimal bigDecimal, int scale, RoundingMode roundingMode) {
		this.bigDecimal = bigDecimal;
		this.roundingMode = roundingMode;
		this.scale = scale;
	}

	public NumberHolder add(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().add(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder subtract(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().subtract(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder multiply(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().multiply(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder divide(NumberHolder numberHolder) {
		BigDecimal bigDecimal;
		try {
			bigDecimal = toBigDecimal().divide(numberHolder.toBigDecimal());
		} catch (ArithmeticException e) {
			logger.error("{}/{} error:{}", toBigDecimal(), numberHolder.toBigDecimal(), e.getMessage());
			bigDecimal = toBigDecimal().divide(numberHolder.toBigDecimal(), scale, roundingMode);
		}
		return new BigDecimalHolder(bigDecimal, scale, roundingMode);
	}

	public NumberHolder divide(NumberHolder numberHolder, int scale, RoundingMode roundingMode) {
		return new BigDecimalHolder(toBigDecimal().divide(numberHolder.toBigDecimal(), scale, roundingMode),
				scale, roundingMode);
	}

	public NumberHolder remainder(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().remainder(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder pow(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().pow(numberHolder.toBigDecimal().intValue()), scale,
				roundingMode);
	}

	public BigDecimal toBigDecimal() {
		return bigDecimal;
	}

	@Override
	public String toString() {
		return bigDecimal.toString();
	}

	public NumberHolder abs() {
		return new BigDecimalHolder(bigDecimal.abs());
	}
}
