package scw.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class BigDecimalHolder extends AbstractNumberHolder {
	private static Logger logger = LoggerUtils
			.getLogger(BigDecimalHolder.class);
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

	public BigDecimalHolder(BigDecimal bigDecimal, int scale,
			RoundingMode roundingMode) {
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

	// 将当前值变为同分母分数
	private FractionHolder toFractionNumberHolder(FractionHolder numberHolder) {
		return new FractionHolder(numberHolder.multiply(numberHolder.getDenominator()),
				numberHolder.getDenominator());
	}

	public NumberHolder add(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).add(fractionHolder);
		}

		return new BigDecimalHolder(toBigDecimal().add(
				numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder subtract(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).subtract(fractionHolder);
		}
		
		return new BigDecimalHolder(toBigDecimal().subtract(
				numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder multiply(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).multiply(fractionHolder);
		}
		
		return new BigDecimalHolder(toBigDecimal().multiply(
				numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder divide(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).divide(fractionHolder);
		}
		
		BigDecimal left = toBigDecimal();
		BigDecimal right = numberHolder.toBigDecimal();
		BigDecimal bigDecimal;
		try {
			bigDecimal = left.divide(right);
		} catch (ArithmeticException e) {
			bigDecimal = left.divide(right, scale, roundingMode);
			logger.error("{}/{} Compulsory use value {} error:{}", left, right,
					bigDecimal, e.getMessage());
		}
		return new BigDecimalHolder(bigDecimal, scale, roundingMode);
	}

	public NumberHolder divide(NumberHolder numberHolder, int scale,
			RoundingMode roundingMode) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).divide(fractionHolder);
		}
		
		return new BigDecimalHolder(toBigDecimal().divide(
				numberHolder.toBigDecimal(), scale, roundingMode), scale,
				roundingMode);
	}

	public NumberHolder remainder(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).remainder(fractionHolder);
		}
		
		return new BigDecimalHolder(toBigDecimal().remainder(
				numberHolder.toBigDecimal()), scale, roundingMode);
	}

	public NumberHolder pow(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {// 如果是分数
			FractionHolder fractionHolder = (FractionHolder)numberHolder;
			return toFractionNumberHolder(fractionHolder).remainder(fractionHolder);
		}
		
		return new BigDecimalHolder(toBigDecimal().pow(
				numberHolder.toBigDecimal().intValue()), scale, roundingMode);
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
