package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.domain.Value;

/**
 * 分数类，实现高精度有理数运算。 该类以分子/分母形式表示有理数，支持分数的各种数学运算，
 * 所有运算均保持分数形式并自动进行约分，确保计算过程中的精度不会丢失。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>分数运算：支持加减乘除、取余、幂等基本数学运算</li>
 * <li>自动约分：所有运算结果都会自动约分为最简分数</li>
 * <li>高精度计算：基于BigDecimal实现，避免浮点数精度问题</li>
 * <li>灵活转换：支持与其他数值类型的相互转换</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>金融计算：需要精确计算的货币和利率计算</li>
 * <li>科学计算：需要高精度和无精度丢失的计算场景</li>
 * <li>游戏开发：需要精确数值的游戏逻辑计算</li>
 * <li>算法实现：依赖精确数值的算法（如几何计算）</li>
 * </ul>
 *
 * <p>
 * 示例用法：
 * 
 * <pre class="code">
 * // 创建分数
 * Fraction a = new Fraction("1", "3");
 * Fraction b = new Fraction("2", "5");
 * 
 * // 分数运算
 * Fraction sum = a.add(b); // 1/3 + 2/5 = 11/15
 * Fraction product = a.multiply(b); // 1/3 * 2/5 = 2/15
 * 
 * // 转换为小数
 * BigDecimal decimal = sum.getAsBigDecimal(); // 0.7333...
 * 
 * // 约分
 * Fraction simplified = sum.reduction(); // 11/15（已是最简）
 * </pre>
 *
 * @author soeasy.run
 * @see RationalNumber
 * @see NumberValue
 */
@Getter
@Setter
public class Fraction extends RationalNumber {
	private static final long serialVersionUID = 1L;

	/** 零分数常量 */
	public static final Fraction ZERO = new Fraction(BigIntegerValue.ZERO, BigIntegerValue.ONE);

	/** 分子 */
	private final NumberValue molecule;
	/** 分母 */
	private final NumberValue denominator;

	/**
	 * 最大公约数计算器，用于分数约分。
	 */
	@NonNull
	private GreatestCommonDivisor greatestCommonDivisor = GreatestCommonDivisor.DIVISION_ALGORITHM;

	/**
	 * 从BigDecimal创建分数。
	 * <p>
	 * 构造时会自动将小数转换为分数形式，例如0.5会转换为1/2。
	 *
	 * @param bigDecimal 要转换的BigDecimal，不可为null
	 */
	public Fraction(BigDecimal bigDecimal) {
		this(new BigDecimalValue(bigDecimal), new BigDecimalValue(BigDecimal.ONE));
	}

	/**
	 * 使用NumberValue类型的分子和分母创建分数。
	 * <p>
	 * 这是最通用的构造函数，允许使用任何NumberValue的实现作为分子和分母。
	 *
	 * @param molecule    分子，不可为null
	 * @param denominator 分母，不可为null
	 * @throws IllegalArgumentException 如果分母为零
	 */
	public Fraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		// 检查分母是否为零
		if(denominator.compareTo(NumberValue.ZERO) == 0) {
			throw new IllegalArgumentException("The denominator cannot be zero");
		}
		this.molecule = molecule;
		this.denominator = denominator;
	}

	/**
	 * 将另一个数转换为与当前分数同分母的分数。
	 * <p>
	 * 常用于分数加法和减法运算前的准备。
	 *
	 * @param value 要转换的数
	 * @return 与当前分数同分母的新分数
	 */
	public Fraction sameDenominator(NumberValue value) {
		return new Fraction(value.multiply(this.denominator), denominator);
	}

	/**
	 * 分数加法运算。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>如果另一个数是分数，直接通分后分子相加</li>
	 * <li>否则先将其转换为同分母分数再相加</li>
	 * <li>结果自动约分</li>
	 * </ol>
	 *
	 * @param value 加数
	 * @return 加法结果，已约分
	 */
	@Override
	public Fraction add(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction summand = (Fraction) value;
			// 分数相加：(a/b + c/d) = (a*d + c*b)/(b*d)
			return new Fraction(molecule.multiply(summand.denominator).add(summand.molecule.multiply(denominator)),
					denominator.multiply(summand.denominator)).reduction();
		} else {
			return add(sameDenominator(value));
		}
	}

	/**
	 * 分数减法运算。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>如果另一个数是分数，直接通分后分子相减</li>
	 * <li>否则先将其转换为同分母分数再相减</li>
	 * <li>结果自动约分</li>
	 * </ol>
	 *
	 * @param value 减数
	 * @return 减法结果，已约分
	 */
	@Override
	public Fraction subtract(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction minuend = (Fraction) value;
			// 分数相减：(a/b - c/d) = (a*d - c*b)/(b*d)
			return new Fraction(molecule.multiply(minuend.denominator).subtract(minuend.molecule.multiply(denominator)),
					denominator.multiply(minuend.denominator)).reduction();
		} else {
			return subtract(sameDenominator(value));
		}
	}

	/**
	 * 分数乘法运算。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>如果另一个数是分数，分子分母分别相乘</li>
	 * <li>否则先将其转换为同分母分数再相乘</li>
	 * <li>结果自动约分</li>
	 * </ol>
	 *
	 * @param value 乘数
	 * @return 乘法结果，已约分
	 */
	@Override
	public Fraction multiply(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction multiplicand = (Fraction) value;
			// 分数乘法：(a/b) * (c/d) = (a*c)/(b*d)
			return new Fraction(molecule.multiply(multiplicand.molecule),
					denominator.multiply(multiplicand.denominator)).reduction();
		} else {
			return multiply(sameDenominator(value));
		}
	}

	/**
	 * 计算当前分数的倒数。
	 * <p>
	 * 例如，3/4的倒数是4/3。
	 *
	 * @return 倒数分数
	 */
	public Fraction reciprocal() {
		return new Fraction(denominator, molecule);
	}

	/**
	 * 分数除法运算。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>如果另一个数是分数，乘以其倒数</li>
	 * <li>否则先将其转换为同分母分数再进行除法</li>
	 * <li>结果自动约分</li>
	 * </ol>
	 *
	 * @param value 除数
	 * @return 除法结果，已约分
	 * @throws ArithmeticException 如果除数为零
	 */
	@Override
	public Fraction divide(NumberValue value) {
		if (value instanceof Fraction) {
			// 分数除法：(a/b) / (c/d) = (a/b) * (d/c)
			return multiply(((Fraction) value).reciprocal()).reduction();
		} else {
			return divide(sameDenominator(value));
		}
	}

	/**
	 * 计算分子除以分母的余数。
	 * <p>
	 * 例如，对于分数7/3，余数为1。
	 *
	 * @return 余数
	 */
	public NumberValue remainder() {
		return molecule.remainder(denominator);
	}

	/**
	 * 分数取余运算。
	 * <p>
	 * 实现逻辑：将分数转换为BigDecimal后进行取余运算。 此方法可能会损失精度，适用于不需要保持分数形式的场景。
	 *
	 * @param value 除数
	 * @return 余数，以BigDecimalValue形式表示
	 */
	@Override
	public NumberValue remainder(NumberValue value) {
		return new BigDecimalValue(getAsBigDecimal().remainder(value.getAsBigDecimal()));
	}

	/**
	 * 分数幂运算。
	 * <p>
	 * 实现逻辑：分子和分母分别进行幂运算。 注意：此方法不进行约分，结果可能需要手动调用reduction()进行约分。
	 *
	 * @param value 指数
	 * @return 幂运算结果
	 */
	@Override
	public NumberValue pow(NumberValue value) {
		// 分数的幂：(a/b)^n = (a^n)/(b^n)
		return new Fraction(molecule.pow(value), denominator.pow(value));
	}

	/**
	 * 将分数转换为BigDecimal表示。
	 * <p>
	 * 此方法可能会损失精度，特别是对于无限循环小数。
	 *
	 * @return BigDecimal表示
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return molecule.divide(denominator).getAsBigDecimal();
	}

	/**
	 * 计算分数的绝对值。
	 *
	 * @return 绝对值分数
	 */
	public Fraction abs() {
		return new Fraction(molecule.abs(), denominator.abs());
	}

	/**
	 * 格式化数值为字符串，确保复杂分数正确显示。
	 *
	 * @param value 要格式化的数值
	 * @return 格式化后的字符串
	 */
	private String toString(NumberValue value) {
		return (value instanceof Fraction) ? ("(" + value + ")") : value.toString();
	}

	/**
	 * 将分数转换为字符串表示，格式为"分子/分母"。
	 * <p>
	 * 例如，分数1/2会表示为"1/2"，复杂分数会用括号包裹。
	 *
	 * @return 分数的字符串表示
	 */
	@Override
	public String getAsString() {
		return toString(molecule) + "/" + toString(denominator);
	}

	/**
	 * 比较两个分数的大小。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>如果另一个值是分数，直接比较(a/b)与(c/d)等价于比较a*d与c*b</li>
	 * <li>否则先将其转换为同分母分数再比较</li>
	 * </ol>
	 *
	 * @param value 要比较的值
	 * @return 比较结果：负整数表示小于，零表示等于，正整数表示大于
	 */
	public int compareTo(Value value) {
		if (value.isNumber()) {
			if (value instanceof Fraction) {
				// 比较两个分数：(a/b) 与 (c/d) 等价于比较 (a*d) 与 (c*b)
				return molecule.multiply(((Fraction) value).denominator)
						.compareTo(denominator.multiply(((Fraction) value).molecule));
			} else {
				return compareTo(sameDenominator(value.getAsNumber()));
			}
		}
		return super.compareTo(value);
	}

	/**
	 * 将分数约分为最简形式。
	 * <p>
	 * 实现逻辑：
	 * <ol>
	 * <li>如果分子或分母是分数，先进行展开</li>
	 * <li>计算分子和分母的最大公约数</li>
	 * <li>分子和分母分别除以最大公约数</li>
	 * </ol>
	 *
	 * @return 最简分数
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
		BigDecimal gcd = getGreatestCommonDivisor().apply(molecule, denominator);
		if (gcd.equals(BigDecimal.ONE)) {
			// 如果最大公约数是1，说明已经是最简分数
			return this;
		}

		return new Fraction(new BigDecimalValue(molecule.divide(gcd)), new BigDecimalValue(denominator.divide(gcd)));
	}

	/**
	 * 将分数转换为BigInteger表示。
	 * <p>
	 * 此方法会截断小数部分，只保留整数部分。
	 *
	 * @return BigInteger表示
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return molecule.divide(denominator).getAsBigInteger();
	}

	/**
	 * 计算分数的哈希值。
	 * <p>
	 * 哈希值由分子和分母的哈希值组合而成。
	 *
	 * @return 哈希值
	 */
	@Override
	public int hashCode() {
		return molecule.hashCode() + denominator.hashCode();
	}
}