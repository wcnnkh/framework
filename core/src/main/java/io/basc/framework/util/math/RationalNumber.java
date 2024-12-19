package io.basc.framework.util.math;

/**
 * 有理数定义
 * 
 * @author shuchaowen
 *
 */
public abstract class RationalNumber extends NumberValue {
	private static final long serialVersionUID = 1L;

	@Override
	public NumberValue abs() {
		return toFraction(this).abs();
	}

	@Override
	public NumberValue add(NumberValue value) {
		return toFraction(this).add(value);
	}

	@Override
	public NumberValue divide(NumberValue value) {
		return toFraction(this).divide(value);
	}

	@Override
	public NumberValue multiply(NumberValue value) {
		return toFraction(this).multiply(value);
	}

	@Override
	public NumberValue pow(NumberValue value) {
		return toFraction(this).pow(value);
	}

	@Override
	public NumberValue remainder(NumberValue value) {
		return toFraction(this).remainder(value);
	}

	@Override
	public NumberValue subtract(NumberValue value) {
		return toFraction(this).subtract(value);
	}

	private Fraction toFraction(NumberValue value) {
		if (value instanceof Fraction) {
			return (Fraction) value;
		}
		return new Fraction(value, BigIntegerValue.ONE);
	}

}
