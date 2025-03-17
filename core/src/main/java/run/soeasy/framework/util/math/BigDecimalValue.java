package run.soeasy.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import run.soeasy.framework.util.Value;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class BigDecimalValue extends RationalNumber {
	private static final long serialVersionUID = 1L;

	public static final BigDecimalValue ZERO = new BigDecimalValue(BigDecimal.ZERO);

	private static Logger logger = LogManager.getLogger(BigDecimalValue.class);
	private static final int DEFAULT_SCALE = 64;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

	private final BigDecimal value;
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

	public BigDecimalValue(BigDecimal value, int scale, RoundingMode roundingMode) {
		this.value = value;
		this.roundingMode = roundingMode;
		this.scale = scale;
	}

	public final int getScale() {
		return scale;
	}

	public final RoundingMode getRoundingMode() {
		return roundingMode;
	}

	@Override
	public NumberValue add(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().add(value.getAsBigDecimal()), scale, roundingMode);
	}

	@Override
	public NumberValue subtract(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().subtract(value.getAsBigDecimal()), scale, roundingMode);
	}

	@Override
	public NumberValue multiply(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().multiply(value.getAsBigDecimal()), scale, roundingMode);
	}

	@Override
	public NumberValue divide(NumberValue value) {
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

	@Override
	public NumberValue remainder(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().remainder(value.getAsBigDecimal()), scale, roundingMode);
	}

	@Override
	public NumberValue pow(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().pow(value.getAsBigDecimal().intValueExact()), scale, roundingMode);
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return value;
	}

	@Override
	public BigInteger getAsBigInteger() {
		return value.toBigInteger();
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	public NumberValue abs() {
		return new BigDecimalValue(value.abs());
	}

	@Override
	public String getAsString() {
		return value.toString();
	}

	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigDecimal value = o.getAsBigDecimal();
			return this.value.compareTo(value);
		}
		return super.compareTo(o);
	}

}
