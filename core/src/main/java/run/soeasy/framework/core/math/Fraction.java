package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.math.gcd.GreatestCommonDivisor;

/**
 * 分数
 * 
 * @author soeasy.run
 *
 */
public class Fraction extends RationalNumber {
	private static final long serialVersionUID = 1L;

	public static final Fraction ZERO = new Fraction(BigIntegerValue.ZERO, BigIntegerValue.ONE);

	/**
	 * 分子
	 */
	private final NumberValue molecule;
	/**
	 * 分母
	 */
	private final NumberValue denominator;

	private GreatestCommonDivisor greatestCommonDivisor = MathUtils.getGreatestCommonDivisor();

	public Fraction(BigDecimal bigDecimal) {
		this(new BigDecimalValue(bigDecimal), new BigDecimalValue(BigDecimal.ONE));
	}

	/**
	 * 构造一个分数
	 * 
	 * @param molecule    分子
	 * @param denominator 分母
	 */
	public Fraction(String molecule, String denominator) {
		this(new BigDecimalValue(molecule), new BigDecimalValue(denominator));
	}

	/**
	 * 构造一个分数
	 * 
	 * @param molecule     分子
	 * @param denominator  分母
	 * @param scale
	 * @param roundingMode
	 */
	public Fraction(String molecule, String denominator, int scale, RoundingMode roundingMode) {
		this(new BigDecimalValue(molecule, scale, roundingMode), new BigDecimalValue(denominator, scale, roundingMode));
	}

	/**
	 * 构造一个分数
	 * 
	 * @param molecule    分子
	 * @param denominator 分母
	 */
	public Fraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		this.molecule = molecule;
		this.denominator = denominator;
	}

	public GreatestCommonDivisor getGreatestCommonDivisor() {
		return greatestCommonDivisor;
	}

	public void setGreatestCommonDivisor(GreatestCommonDivisor greatestCommonDivisor) {
		this.greatestCommonDivisor = greatestCommonDivisor;
	}

	/**
	 * 通分
	 * 
	 * @param value
	 * @return
	 */
	public Fraction sameDenominator(NumberValue value) {
		return new Fraction(value.multiply(this.denominator), denominator);
	}

	@Override
	public Fraction add(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction summand = (Fraction) value;
			// 分数相加，将分数转为同分母，分子相加
			return new Fraction(molecule.multiply(summand.denominator).add(summand.molecule.multiply(denominator)),
					denominator.multiply(summand.denominator)).reduction();
		} else {
			return add(sameDenominator(value));
		}
	}

	@Override
	public Fraction subtract(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction minuend = (Fraction) value;
			// 分数相减，将分数转为同分母，分子相减
			return new Fraction(molecule.multiply(minuend.denominator).subtract(minuend.molecule.multiply(denominator)),
					denominator.multiply(minuend.denominator)).reduction();
		} else {
			return subtract(sameDenominator(value));
		}
	}

	@Override
	public Fraction multiply(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction multiplicand = (Fraction) value;
			// 分数乘法，将分子分母各相乘
			return new Fraction(molecule.multiply(multiplicand.molecule),
					denominator.multiply(multiplicand.denominator)).reduction();
		} else {
			return multiply(sameDenominator(value));
		}
	}

	/**
	 * 倒数
	 * 
	 * @return 倒数
	 */
	public Fraction reciprocal() {
		return new Fraction(denominator, molecule);
	}

	@Override
	public Fraction divide(NumberValue value) {
		if (value instanceof Fraction) {
			// 除以一个数等于乘以这个数的倒数
			return multiply(((Fraction) value).reciprocal()).reduction();
		} else {
			return divide(sameDenominator(value));
		}
	}

	/**
	 * 将分子分母相除，取余
	 * 
	 * @return
	 */
	public NumberValue remainder() {
		return molecule.remainder(denominator);
	}

	@Override
	public NumberValue remainder(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().remainder(value.getAsBigDecimal()));
	}

	@Override
	public NumberValue pow(NumberValue value) {
		// 分数的指数运算,将分子分母分别进行指数运算
		return new Fraction(molecule.pow(value), denominator.pow(value));
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return molecule.divide(denominator).getAsBigDecimal();
	}

	/**
	 * 分子
	 * 
	 * @return
	 */
	public final NumberValue getMolecule() {
		return molecule;
	}

	/**
	 * 分母
	 * 
	 * @return
	 */
	public final NumberValue getDenominator() {
		return denominator;
	}

	public Fraction abs() {
		return new Fraction(molecule.abs(), denominator.abs());
	}

	private String toString(NumberValue value) {
		return (value instanceof Fraction) ? ("(" + value + ")") : value.toString();
	}

	@Override
	public String getAsString() {
		return toString(molecule) + "/" + toString(denominator);
	}

	public int compareTo(Value value) {
		if (value.isNumber()) {
			if (value instanceof Fraction) {
				return molecule.multiply(((Fraction) value).denominator)
						.compareTo(denominator.multiply(((Fraction) value).molecule));
			} else {
				return compareTo(sameDenominator(value.getAsNumber()));
			}
		}
		return super.compareTo(value);
	}

	/**
	 * 约分
	 * 
	 * @return
	 */
	public Fraction reduction() {
		if (molecule instanceof Fraction) {
			return ((Fraction) molecule).divide(denominator).reduction();
		}

		if (denominator instanceof Fraction) {
			return sameDenominator(molecule).divide((Fraction) denominator).reciprocal();
		}

		BigDecimal molecule = this.molecule.getAsBigDecimal();
		if (molecule.doubleValue() == 0) {
			return ZERO;
		}

		if (molecule.doubleValue() == 1) {
			return this;
		}

		BigDecimal denominator = this.denominator.getAsBigDecimal();
		BigDecimal gcd = getGreatestCommonDivisor().gcd(molecule, denominator);
		if (gcd.equals(BigDecimal.ONE)) {// 如果最大公约数量1说明不能进行约分
			return this;
		}

		return new Fraction(new BigDecimalValue(molecule.divide(gcd)), new BigDecimalValue(denominator.divide(gcd)));
	}

	@Override
	public BigInteger getAsBigInteger() {
		return molecule.divide(denominator).getAsBigInteger();
	}

	@Override
	public int hashCode() {
		return molecule.hashCode() + denominator.hashCode();
	}
}
