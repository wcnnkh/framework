package io.basc.framework.util.math;

public abstract class RationalNumber extends NumberValue {
	private static final long serialVersionUID = 1L;

	public int compareTo(NumberValue o) {
		return getAsBigDecimal().compareTo(o.getAsBigDecimal());
	}

	// 将当前值变为同分母分数
	private Fraction toFraction(Fraction value) {
		return new Fraction(multiply(value.getDenominator()), value.getDenominator());
	}

	public final NumberValue add(NumberValue value) {
		if (value instanceof Fraction) {
			return add((Fraction) value);
		}
		return addInternal(value);
	}

	protected abstract NumberValue addInternal(NumberValue value);

	public Fraction add(Fraction fraction) {
		return toFraction(fraction).add(fraction);
	}

	public final NumberValue subtract(NumberValue value) {
		if (value instanceof Fraction) {
			return subtract((Fraction) value);
		}
		return subtractInternal(value);
	}

	protected abstract NumberValue subtractInternal(NumberValue value);

	public Fraction subtract(Fraction fraction) {
		Fraction value = toFraction(fraction);
		return fraction.subtract(value);
	}

	public final NumberValue multiply(NumberValue value) {
		if (value instanceof Fraction) {
			return multiply((Fraction) value);
		}
		return multiplyInternal(value);
	}

	protected abstract NumberValue multiplyInternal(NumberValue value);

	public Fraction multiply(Fraction value) {
		return toFraction(value).multiply(value);
	}

	public final NumberValue divide(NumberValue value) {
		if (value instanceof Fraction) {
			return divide((Fraction) value);
		}
		return divideInternal(value);
	}

	protected abstract NumberValue divideInternal(NumberValue value);

	public Fraction divide(Fraction fraction) {
		return toFraction(fraction).divide(fraction);
	}

	public final NumberValue remainder(NumberValue value) {
		if (value instanceof Fraction) {
			return remainder((Fraction) value);
		}
		return remainderInternal(value);
	}

	protected abstract NumberValue remainderInternal(NumberValue value);

	public NumberValue remainder(Fraction fraction) {
		return toFraction(fraction).remainder(fraction);
	}

	public final NumberValue pow(NumberValue value) {
		if (value instanceof Fraction) {
			return pow((Fraction) value);
		}
		return powInternal(value);
	}

	protected abstract NumberValue powInternal(NumberValue numberHolder);

	public NumberValue pow(Fraction fraction) {
		return toFraction(fraction).pow(fraction);
	}
}
