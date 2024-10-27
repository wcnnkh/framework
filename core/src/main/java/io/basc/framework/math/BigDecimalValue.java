package io.basc.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;

public class BigDecimalValue extends RationalNumber {
	private static final long serialVersionUID = 1L;

	public static final BigDecimalValue ZERO = new BigDecimalValue(BigDecimal.ZERO);

	private static Logger logger = LogManager.getLogger(BigDecimalValue.class);
	private static final int DEFAULT_SCALE = 64;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

	private final BigDecimal bigDecimal;
	private final int scale;
	private final RoundingMode roundingMode;

	public BigDecimalValue(String number) {
		this(new BigDecimal(number), DEFAULT_SCALE, ROUNDING_MODE);
	}

	public BigDecimalValue(String number, int scale, RoundingMode roundingMode) {
		this(new BigDecimal(number), scale, roundingMode);
	}

	public BigDecimalValue(BigDecimal bigDecimal) {
		this(bigDecimal, DEFAULT_SCALE, ROUNDING_MODE);
	}

	public BigDecimalValue(BigDecimal bigDecimal, int scale, RoundingMode roundingMode) {
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

	protected NumberValue addInternal(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().add(value.getAsBigDecimal()), scale, roundingMode);
	}

	protected NumberValue subtractInternal(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().subtract(value.getAsBigDecimal()), scale, roundingMode);
	}

	protected NumberValue multiplyInternal(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().multiply(value.getAsBigDecimal()), scale, roundingMode);
	}

	protected NumberValue divideInternal(NumberValue value) {
		BigDecimal left = getAsBigDecimal();
		BigDecimal right = value.getAsBigDecimal();
		BigDecimal bigDecimal;
		try {
			bigDecimal = left.divide(right);
		} catch (ArithmeticException e) {
			bigDecimal = left.divide(right, scale, roundingMode);
			logger.error("{}/{} Compulsory use value {} error:{}", left, right, bigDecimal, e.getMessage());
		}
		return new BigDecimalValue(bigDecimal, scale, roundingMode);
	}

	public NumberValue divide(NumberValue value, int scale, RoundingMode roundingMode) {
		if (value instanceof Fraction) {// 如果是分数
			return divide((Fraction) value);
		}

		return new BigDecimalValue(getAsBigDecimal().divide(value.getAsBigDecimal(), scale, roundingMode), scale,
				roundingMode);
	}

	protected NumberValue remainderInternal(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().remainder(value.getAsBigDecimal()), scale, roundingMode);
	}

	protected NumberValue powInternal(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().pow(value.getAsBigDecimal().intValueExact()), scale,
				roundingMode);
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return bigDecimal;
	}

	@Override
	public BigInteger getAsBigInteger() {
		return bigDecimal.toBigInteger();
	}

	@Override
	public String toString() {
		return bigDecimal.toString();
	}

	@Override
	public int hashCode() {
		return bigDecimal.hashCode();
	}

	public NumberValue abs() {
		return new BigDecimalValue(bigDecimal.abs());
	}

	@Override
	public String getAsString() {
		return bigDecimal.toString();
	}

}
