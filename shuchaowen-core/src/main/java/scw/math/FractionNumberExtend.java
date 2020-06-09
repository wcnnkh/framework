package scw.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 分数
 * 
 * @author shuchaowen
 *
 */
public class FractionNumberExtend implements NumberExtend {
	/**
	 * 分子
	 */
	private final NumberExtend molecule;
	/**
	 * 分母
	 */
	private final NumberExtend denominator;

	/**
	 * 构造一个分数
	 * 
	 * @param molecule
	 *            分子
	 * @param denominator
	 *            分母
	 */
	public FractionNumberExtend(String molecule, String denominator) {
		this(new BigDecimalNumberExtend(molecule), new BigDecimalNumberExtend(denominator));
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
	public FractionNumberExtend(String molecule, String denominator, int scale, RoundingMode roundingMode) {
		this(new BigDecimalNumberExtend(molecule, scale, roundingMode),
				new BigDecimalNumberExtend(denominator, scale, roundingMode));
	}

	/**
	 * 构造一个分数
	 * 
	 * @param molecule
	 *            分子
	 * @param denominator
	 *            分母
	 */
	public FractionNumberExtend(NumberExtend molecule, NumberExtend denominator) {
		this.molecule = molecule;
		this.denominator = denominator;
	}

	// 转为同分母分数
	private FractionNumberExtend toFractionNumberExtend(NumberExtend numberExtend) {
		NumberExtend molecule = numberExtend.multiply(this.denominator);
		return new FractionNumberExtend(molecule, denominator);
	}

	public FractionNumberExtend add(NumberExtend numberExtend) {
		if (numberExtend instanceof FractionNumberExtend) {
			FractionNumberExtend summand = (FractionNumberExtend) numberExtend;
			// 分数相加，将分数转为同分母，分子相加
			return new FractionNumberExtend(
					molecule.multiply(summand.denominator).add(summand.molecule.multiply(denominator)),
					denominator.multiply(summand.denominator));
		} else {
			return add(toFractionNumberExtend(numberExtend));
		}
	}

	public FractionNumberExtend subtract(NumberExtend numberExtend) {
		if (numberExtend instanceof FractionNumberExtend) {
			FractionNumberExtend minuend = (FractionNumberExtend) numberExtend;
			// 分数相减，将分数转为同分母，分子相减
			return new FractionNumberExtend(
					molecule.multiply(minuend.denominator).subtract(minuend.molecule.multiply(denominator)),
					denominator.multiply(minuend.denominator));
		} else {
			return subtract(toFractionNumberExtend(numberExtend));
		}
	}

	public FractionNumberExtend multiply(NumberExtend numberExtend) {
		if (numberExtend instanceof FractionNumberExtend) {
			FractionNumberExtend multiplicand = (FractionNumberExtend) numberExtend;
			// 分数乘法，将分子分母各相乘
			return new FractionNumberExtend(molecule.multiply(multiplicand.molecule),
					denominator.multiply(multiplicand.denominator));
		} else {
			return multiply(toFractionNumberExtend(numberExtend));
		}
	}

	/**
	 * 倒数
	 * 
	 * @param fraction
	 * @return
	 */
	public FractionNumberExtend reciprocal() {
		return new FractionNumberExtend(denominator, molecule);
	}

	public FractionNumberExtend divide(NumberExtend numberExtend) {
		if (numberExtend instanceof FractionNumberExtend) {
			// 除以一个数等于乘以这个数的倒数
			return multiply(((FractionNumberExtend) numberExtend).reciprocal());
		} else {
			return divide(toFractionNumberExtend(numberExtend));
		}
	}

	public NumberExtend remainder() {
		return molecule.remainder(denominator);
	}

	public NumberExtend remainder(NumberExtend numberExtend) {
		return divide(numberExtend).remainder();
	}

	public NumberExtend pow(NumberExtend numberExtend) {
		// 分数的指数运算,将分子分母分别进行指数运算
		return new FractionNumberExtend(molecule.pow(numberExtend), denominator.pow(numberExtend));
	}

	public BigDecimal toBigDecimal() {
		return molecule.divide(denominator).toBigDecimal();
	}

	public final NumberExtend getMolecule() {
		return molecule;
	}

	public final NumberExtend getDenominator() {
		return denominator;
	}

	@Override
	public String toString() {
		return molecule + "/" + denominator;
	}
}
