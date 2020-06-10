package scw.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import scw.math.gcd.GreatestCommonDivisor;

/**
 * 分数
 * 
 * @author shuchaowen
 *
 */
public class Fraction implements NumberHolder {
	public static final Fraction ZERO = new Fraction(BigIntegerHolder.ZERO, BigIntegerHolder.ONE);

	/**
	 * 分子
	 */
	private final NumberHolder molecule;
	/**
	 * 分母
	 */
	private final NumberHolder denominator;

	private GreatestCommonDivisor greatestCommonDivisor = MathUtils.getGreatestCommonDivisor();

	/**
	 * 构造一个分数
	 * 
	 * @param molecule
	 *            分子
	 * @param denominator
	 *            分母
	 */
	public Fraction(String molecule, String denominator) {
		this(new BigDecimalHolder(molecule), new BigDecimalHolder(denominator));
	}

	/**
	 * 构造一个分数
	 * 
	 * @param molecule
	 *            分子
	 * @param denominator
	 *            分母
	 * @param scale
	 * @param roundingMode
	 */
	public Fraction(String molecule, String denominator, int scale, RoundingMode roundingMode) {
		this(new BigDecimalHolder(molecule, scale, roundingMode),
				new BigDecimalHolder(denominator, scale, roundingMode));
	}

	/**
	 * 构造一个分数
	 * 
	 * @param molecule
	 *            分子
	 * @param denominator
	 *            分母
	 */
	public Fraction(NumberHolder molecule, NumberHolder denominator) {
		this.molecule = molecule;
		this.denominator = denominator;
	}

	public GreatestCommonDivisor getGreatestCommonDivisor() {
		return greatestCommonDivisor;
	}

	public void setGreatestCommonDivisor(GreatestCommonDivisor greatestCommonDivisor) {
		this.greatestCommonDivisor = greatestCommonDivisor;
	}

	// 转为同分母分数
	private Fraction toFractionNumberHolder(NumberHolder numberHolder) {
		return new Fraction(numberHolder.multiply(this.denominator), denominator);
	}

	public Fraction add(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			Fraction summand = (Fraction) numberHolder;
			// 分数相加，将分数转为同分母，分子相加
			return new Fraction(
					molecule.multiply(summand.denominator).add(summand.molecule.multiply(denominator)),
					denominator.multiply(summand.denominator)).reduction();
		} else {
			return add(toFractionNumberHolder(numberHolder));
		}
	}

	public Fraction subtract(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			Fraction minuend = (Fraction) numberHolder;
			// 分数相减，将分数转为同分母，分子相减
			return new Fraction(
					molecule.multiply(minuend.denominator).subtract(minuend.molecule.multiply(denominator)),
					denominator.multiply(minuend.denominator)).reduction();
		} else {
			return subtract(toFractionNumberHolder(numberHolder));
		}
	}

	public Fraction multiply(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			Fraction multiplicand = (Fraction) numberHolder;
			// 分数乘法，将分子分母各相乘
			return new Fraction(molecule.multiply(multiplicand.molecule),
					denominator.multiply(multiplicand.denominator)).reduction();
		} else {
			return multiply(toFractionNumberHolder(numberHolder));
		}
	}

	/**
	 * 倒数
	 * 
	 * @param fraction
	 * @return
	 */
	public Fraction reciprocal() {
		return new Fraction(denominator, molecule);
	}

	public Fraction divide(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			// 除以一个数等于乘以这个数的倒数
			return multiply(((Fraction) numberHolder).reciprocal()).reduction();
		} else {
			return divide(toFractionNumberHolder(numberHolder));
		}
	}

	/**
	 * 将分子分母相除，取余
	 * @return
	 */
	public NumberHolder remainder() {
		return molecule.remainder(denominator);
	}

	public NumberHolder remainder(NumberHolder numberHolder) {
		return new BigDecimalHolder(toBigDecimal().remainder(numberHolder.toBigDecimal()));
	}

	public NumberHolder pow(NumberHolder numberHolder) {
		// 分数的指数运算,将分子分母分别进行指数运算
		return new Fraction(molecule.pow(numberHolder), denominator.pow(numberHolder));
	}

	public BigDecimal toBigDecimal() {
		return molecule.divide(denominator).toBigDecimal();
	}

	/**
	 * 分子
	 * 
	 * @return
	 */
	public final NumberHolder getMolecule() {
		return molecule;
	}

	/**
	 * 分母
	 * 
	 * @return
	 */
	public final NumberHolder getDenominator() {
		return denominator;
	}

	public Fraction abs() {
		return new Fraction(molecule.abs(), denominator.abs());
	}

	@Override
	public String toString() {
		return molecule + "/" + denominator;
	}

	public int compareTo(NumberHolder o) {
		if (o instanceof Fraction) {
			return molecule.multiply(((Fraction) o).denominator)
					.compareTo(denominator.multiply(((Fraction) o).molecule));
		} else {
			return compareTo(toFractionNumberHolder(o));
		}
	}

	/**
	 * 约分
	 * 
	 * @return
	 */
	public Fraction reduction() {
		BigDecimal molecule = this.molecule.toBigDecimal();
		if (molecule.doubleValue() == 0) {
			return ZERO;
		}

		if (molecule.doubleValue() == 1) {
			return this;
		}

		BigDecimal denominator = this.denominator.toBigDecimal();
		BigDecimal gcd = getGreatestCommonDivisor().gcd(molecule, denominator);
		if (gcd.equals(BigDecimal.ONE)) {// 如果最大公约数量1说明不能进行约分
			return this;
		}

		return new Fraction(new BigDecimalHolder(molecule.divide(gcd)),
				new BigDecimalHolder(denominator.divide(gcd)));
	}
}
