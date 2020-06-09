package scw.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 分数
 * 
 * @author shuchaowen
 *
 */
public class FractionHolder extends AbstractNumberHolder {
	/**
	 * 分子
	 */
	private final NumberHolder molecule;
	/**
	 * 分母
	 */
	private final NumberHolder denominator;

	/**
	 * 构造一个分数
	 * 
	 * @param molecule
	 *            分子
	 * @param denominator
	 *            分母
	 */
	public FractionHolder(String molecule, String denominator) {
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
	public FractionHolder(String molecule, String denominator, int scale, RoundingMode roundingMode) {
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
	public FractionHolder(NumberHolder molecule, NumberHolder denominator) {
		this.molecule = molecule;
		this.denominator = denominator;
	}

	// 转为同分母分数
	private FractionHolder toFractionNumberHolder(NumberHolder numberHolder) {
		return new FractionHolder(numberHolder.multiply(this.denominator), denominator);
	}

	public FractionHolder add(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {
			FractionHolder summand = (FractionHolder) numberHolder;
			// 分数相加，将分数转为同分母，分子相加
			return new FractionHolder(
					molecule.multiply(summand.denominator).add(summand.molecule.multiply(denominator)),
					denominator.multiply(summand.denominator));
		} else {
			return add(toFractionNumberHolder(numberHolder));
		}
	}

	public FractionHolder subtract(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {
			FractionHolder minuend = (FractionHolder) numberHolder;
			// 分数相减，将分数转为同分母，分子相减
			return new FractionHolder(
					molecule.multiply(minuend.denominator).subtract(minuend.molecule.multiply(denominator)),
					denominator.multiply(minuend.denominator));
		} else {
			return subtract(toFractionNumberHolder(numberHolder));
		}
	}

	public FractionHolder multiply(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {
			FractionHolder multiplicand = (FractionHolder) numberHolder;
			// 分数乘法，将分子分母各相乘
			return new FractionHolder(molecule.multiply(multiplicand.molecule),
					denominator.multiply(multiplicand.denominator));
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
	public FractionHolder reciprocal() {
		return new FractionHolder(denominator, molecule);
	}

	public FractionHolder divide(NumberHolder numberHolder) {
		if (numberHolder instanceof FractionHolder) {
			// 除以一个数等于乘以这个数的倒数
			return multiply(((FractionHolder) numberHolder).reciprocal());
		} else {
			return divide(toFractionNumberHolder(numberHolder));
		}
	}

	public NumberHolder remainder() {
		return molecule.remainder(denominator);
	}

	public NumberHolder remainder(NumberHolder numberHolder) {
		return divide(numberHolder).remainder();
	}

	public NumberHolder pow(NumberHolder numberHolder) {
		// 分数的指数运算,将分子分母分别进行指数运算
		return new FractionHolder(molecule.pow(numberHolder), denominator.pow(numberHolder));
	}

	public BigDecimal toBigDecimal() {
		return molecule.divide(denominator).toBigDecimal();
	}

	/**
	 * 分子
	 * @return
	 */
	public final NumberHolder getMolecule() {
		return molecule;
	}

	/**
	 * 分母
	 * @return
	 */
	public final NumberHolder getDenominator() {
		return denominator;
	}
	
	public FractionHolder abs() {
		return new FractionHolder(molecule.abs(), denominator.abs());
	}

	@Override
	public String toString() {
		return molecule + "/" + denominator;
	}
	
	@Override
	public int compareTo(NumberHolder o) {
		if(o instanceof FractionHolder){
			return molecule.multiply(((FractionHolder) o).denominator).compareTo(denominator.multiply(((FractionHolder) o).molecule));
		}else{
			return compareTo(toFractionNumberHolder(o));
		}
	}
}
