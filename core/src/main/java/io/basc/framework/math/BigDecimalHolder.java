package io.basc.framework.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class BigDecimalHolder extends AbstractNumberHolder {
	private static final long serialVersionUID = 1L;

	public static final BigDecimalHolder ZERO = new BigDecimalHolder(BigDecimal.ZERO);

	private static Logger logger = LogManager.getLogger(BigDecimalHolder.class);
	private static final int DEFAULT_SCALE = 64;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

	private final BigDecimal bigDecimal;
	private final int scale;
	private final RoundingMode roundingMode;

	public BigDecimalHolder(String number) {
		this(new BigDecimal(number), DEFAULT_SCALE, ROUNDING_MODE);
	}

	public BigDecimalHolder(String number, int scale, RoundingMode roundingMode) {
		this(new BigDecimal(number), scale, roundingMode);
	}

	public BigDecimalHolder(BigDecimal bigDecimal) {
		this(bigDecimal, DEFAULT_SCALE, ROUNDING_MODE);
	}

	public BigDecimalHolder(BigDecimal bigDecimal, int scale, RoundingMode roundingMode) {
		this.bigDecimal = bigDecimal;
		this.roundingMode = roundingMode;
		this.scale = scale;
	}

	public final int getScale() {
		return scale;
	}

	public final RoundingMode getRoundingMode() {
		return roundingMode;
	}

	protected NumberHolder addInternal(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().add(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	protected NumberHolder subtractInternal(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().subtract(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	protected NumberHolder multiplyInternal(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().multiply(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	protected NumberHolder divideInternal(NumberHolder numberHolder) {
		BigDecimal left = toBigDecimal();
		BigDecimal right = numberHolder.toBigDecimal();
		BigDecimal bigDecimal;
		try {
			bigDecimal = left.divide(right);
		} catch (ArithmeticException e) {
			bigDecimal = left.divide(right, scale, roundingMode);
			logger.error("{}/{} Compulsory use value {} error:{}", left, right, bigDecimal, e.getMessage());
		}
		return new BigDecimalHolder(bigDecimal, scale, roundingMode);
	}

	public NumberHolder divide(NumberHolder numberHolder, int scale, RoundingMode roundingMode) {
		if (numberHolder instanceof Fraction) {// 如果是分数
			return divide((Fraction) numberHolder);
		}

		return new BigDecimalHolder(toBigDecimal().divide(numberHolder.toBigDecimal(), scale, roundingMode), scale,
				roundingMode);
	}

	protected NumberHolder remainderInternal(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().remainder(numberHolder.toBigDecimal()), scale, roundingMode);
	}

	protected NumberHolder powInternal(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().pow(numberHolder.toBigDecimal().intValueExact()), scale,
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
