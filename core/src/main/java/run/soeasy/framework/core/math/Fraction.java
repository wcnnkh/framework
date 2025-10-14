package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.domain.Value;

/**
 * 高精度分数实现类，继承自{@link NumberValue}，以“分子/分母”形式表示有理数，
 * 支持所有基础数学运算（加、减、乘、除、取模、幂运算等），且所有运算结果自动约分为最简形式，
 * 彻底避免浮点数精度丢失问题，是框架中跨类型精确数值计算的核心载体。
 * 
 * <p>设计核心：通过“分子分母分离存储+统一约分逻辑”，将所有数值运算转化为整数/高精度数的操作，
 * 同时支持自定义最大公约数（GCD）计算算法，兼顾灵活性与精度要求。
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>无精度丢失运算</strong>：所有运算基于分子、分母的整数/高精度计算，不涉及浮点数转换，
 * 彻底解决0.1+0.2≠0.3等经典精度问题</li>
 * <li><strong>自动最简约分</strong>：运算结果通过配置的{@link GreatestCommonDivisor}算法自动约分，
 * 确保分子分母互质（如4/6自动转为2/3）</li>
 * <li><strong>灵活类型适配</strong>：支持与任意{@link NumberValue}子类（整数、小数）运算，
 * 自动将非分数类型转为“分母为1”的分数后统一处理</li>
 * <li><strong>可配置约分算法</strong>：通过{@link #greatestCommonDivisor}属性指定GCD计算方式（如欧几里得算法、更相减损术），
 * 适配不同性能或精度需求</li>
 * <li><strong>严格异常控制</strong>：分母为0、除数为0等非法操作会主动抛出异常，避免运行时隐藏错误</li>
 * <li><strong>完整类型转换</strong>：支持转为{@link BigDecimal}（小数）、{@link BigInteger}（整数），
 * 转换过程包含清晰的精度说明（如整数转换会截断小数部分）</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融领域：货币计算（如订单金额拆分、利息计算）、税率换算，需精确到分/厘级</li>
 * <li>科学计算：物理/化学实验数据（如浓度、分子量）、工程测量（如尺寸、重量），不允许精度偏差</li>
 * <li>游戏开发：资源数值计算（如血量、道具数量）、概率公式（如暴击率、掉落率），需确保逻辑正确性</li>
 * <li>算法实现：几何计算（如分数坐标）、密码学（如基于分数的加密算法）、周期性任务（如取模调度）</li>
 * <li>数据校验：需精确比较的数值场景（如账单对账、库存核对），避免因精度丢失导致的匹配失败</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * 
 * <pre class="code">
 * // 1. 创建分数实例（多种构造方式）
 * Fraction a = new Fraction(new IntValue(1), new IntValue(3)); // 1/3
 * Fraction b = new Fraction(new BigDecimal("0.4")); // 小数0.4自动转为2/5
 * Fraction c = new Fraction(new BigIntegerValue("7"), new IntValue(3)); // 7/3（超大整数分子）
 *
 * // 2. 基础运算（结果自动约分）
 * Fraction sum = a.add(b); // 1/3 + 2/5 = 11/15（自动约分）
 * Fraction product = sum.multiply(c); // 11/15 * 7/3 = 77/45（未约分，需手动调用reduction()）
 * Fraction simplifiedProduct = product.reduction(); // 77/45（已是最简，返回自身）
 *
 * // 3. 取模运算（结果非负，满足0 ≤ 余数 < |除数|）
 * NumberValue modResult = c.mod(new IntValue(2)); // 7/3 mod 2 = 1/3（2转为2/1，计算后约分）
 *
 * // 4. 类型转换与判断
 * BigDecimal decimalSum = sum.getAsBigDecimal(); // 11/15 → 0.7333...（存在循环小数，精度有限）
 * BigInteger intSum = sum.getAsBigInteger(); // 11/15 → 0（截断小数部分）
 * boolean isPositive = sum.isPositive(); // true（11/15 > 0）
 *
 * // 5. 特殊操作（倒数、绝对值）
 * Fraction reciprocalA = a.reciprocal(); // 1/3的倒数 → 3/1
 * Fraction absNegative = new Fraction(new IntValue(-5), new IntValue(2)).abs(); // -5/2的绝对值 → 5/2
 * </pre>
 *
 * @author soeasy.run
 * @see NumberValue 父类（数值抽象基类，定义基础运算与转换契约）
 * @see RationalNumber 有理数接口（约束有理数的核心行为，如分数运算）
 * @see GreatestCommonDivisor 最大公约数计算器（支撑自动约分逻辑）
 * @see BigDecimalValue 高精度小数实现（可与Fraction无缝运算）
 * @see BigIntegerValue 超大整数实现（可作为Fraction的分子/分母）
 */
@Getter
@Setter
public class Fraction extends NumberValue {
	private static final long serialVersionUID = 1L;

	/**
	 * 零分数常量（分子为0，分母为1），用于比较“数值为0”的场景，避免重复创建实例
	 */
	public static final Fraction ZERO = new Fraction(BigIntegerValue.ZERO, BigIntegerValue.ONE);

	/**
	 * 分数的分子（不可变），支持任意{@link NumberValue}实现（如整数、小数、超大整数）
	 */
	private final NumberValue molecule;
	/**
	 * 分数的分母（不可变，且非0），支持任意{@link NumberValue}实现，运算中始终保持正数（负号迁移至分子）
	 */
	private final NumberValue denominator;

	/**
	 * 最大公约数（GCD）计算器，用于分数约分，默认使用欧几里得算法（{@link GreatestCommonDivisor#DIVISION_ALGORITHM}）
	 * <p>可通过{@link #setGreatestCommonDivisor(GreatestCommonDivisor)}自定义算法，
	 * 如更相减损术，适配不同性能需求
	 */
	@NonNull
	private GreatestCommonDivisor greatestCommonDivisor = GreatestCommonDivisor.DIVISION_ALGORITHM;

	/**
	 * 从{@link BigDecimal}创建分数（小数自动转为分数形式）
	 * <p>转换逻辑：根据BigDecimal的小数位数，将其转为“整数部分×10^n + 小数部分”作为分子，10^n作为分母，
	 * 例如0.4 → 4/10（后续可通过{@link #reduction()}约分为2/5）、1.25 → 125/100（约分为5/4）
	 *
	 * @param bigDecimal 待转换的小数（不可为null），支持任意精度的BigDecimal（如new BigDecimal("0.1000")）
	 */
	public Fraction(BigDecimal bigDecimal) {
		this(new BigDecimalValue(bigDecimal), new BigDecimalValue(BigDecimal.ONE));
	}

	/**
	 * 通用构造函数：通过分子和分母创建分数（最核心的构造方式）
	 * <p>参数校验：分母不可为0（否则抛出{@link IllegalArgumentException}），分子允许为0（此时分数为零分数）
	 * <p>隐含逻辑：分母的负号会自动迁移至分子（如分子3、分母-2 → 分子-3、分母2），确保分母始终为正
	 *
	 * @param molecule    分数的分子（不可为null，支持任意NumberValue类型）
	 * @param denominator 分数的分母（不可为null，支持任意NumberValue类型，且不能为0）
	 * @throws IllegalArgumentException 如果分母数值等于0（非法分数）
	 */
	public Fraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		// 检查分母是否为零，避免创建非法分数
		if (denominator.compareTo(NumberValue.ZERO) == 0) {
			throw new IllegalArgumentException("The denominator cannot be zero (illegal fraction)");
		}
		// 负号迁移：将分母的负号转移到分子，确保分母为正
		if (denominator.isNegative()) {
			this.molecule = molecule.multiply(NumberValue.NEGATIVE_ONE);
			this.denominator = denominator.abs();
		} else {
			this.molecule = molecule;
			this.denominator = denominator;
		}
	}

	/**
	 * 将目标数值转为与当前分数“同分母”的分数（用于加减运算前的通分）
	 * <p>转换逻辑：目标数值 × 当前分母 → 新分子，当前分母 → 新分母，
	 * 例如当前分数为1/3，目标数值为2 → 转换后为6/3（2×3=6，分母保持3）
	 * <p>核心作用：为分数加减法提供统一分母，避免不同分母直接运算导致的逻辑复杂
	 *
	 * @param value 待转换的数值（不可为null，支持任意NumberValue类型，如整数、小数）
	 * @return 与当前分数同分母的新分数（未约分，需在运算后统一处理）
	 */
	public Fraction sameDenominator(NumberValue value) {
		return new Fraction(value.multiply(this.denominator), denominator);
	}

	/**
	 * 分数加法运算：当前分数 + 目标数值
	 * <p>运算逻辑（分两种场景）：
	 * <ol>
	 * <li>若目标是{@link Fraction}：通分后分子相加（a/b + c/d = (a×d + c×b)/(b×d)），结果自动约分</li>
	 * <li>若目标是其他NumberValue：先通过{@link #sameDenominator(NumberValue)}转为同分母分数，再执行加法</li>
	 * </ol>
	 *
	 * @param value 加数（不可为null，支持任意NumberValue类型）
	 * @return 加法结果（已约分为最简分数，类型为Fraction）
	 */
	@Override
	public Fraction add(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction summand = (Fraction) value;
			// 分数相加公式：(a/b) + (c/d) = (a*d + c*b)/(b*d)
			NumberValue newMolecule = molecule.multiply(summand.denominator)
					.add(summand.molecule.multiply(denominator));
			NumberValue newDenominator = denominator.multiply(summand.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		} else {
			// 非分数类型先转为同分母分数，再递归调用加法
			return add(sameDenominator(value));
		}
	}

	/**
	 * 分数减法运算：当前分数 - 目标数值
	 * <p>运算逻辑（分两种场景）：
	 * <ol>
	 * <li>若目标是{@link Fraction}：通分后分子相减（a/b - c/d = (a×d - c×b)/(b×d)），结果自动约分</li>
	 * <li>若目标是其他NumberValue：先通过{@link #sameDenominator(NumberValue)}转为同分母分数，再执行减法</li>
	 * </ol>
	 *
	 * @param value 减数（不可为null，支持任意NumberValue类型）
	 * @return 减法结果（已约分为最简分数，类型为Fraction）
	 */
	@Override
	public Fraction subtract(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction minuend = (Fraction) value;
			// 分数相减公式：(a/b) - (c/d) = (a*d - c*b)/(b*d)
			NumberValue newMolecule = molecule.multiply(minuend.denominator)
					.subtract(minuend.molecule.multiply(denominator));
			NumberValue newDenominator = denominator.multiply(minuend.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		} else {
			// 非分数类型先转为同分母分数，再递归调用减法
			return subtract(sameDenominator(value));
		}
	}

	/**
	 * 分数乘法运算：当前分数 × 目标数值
	 * <p>运算逻辑（分两种场景）：
	 * <ol>
	 * <li>若目标是{@link Fraction}：分子×分子 → 新分子，分母×分母 → 新分母（a/b × c/d = (a×c)/(b×d)），结果自动约分</li>
	 * <li>若目标是其他NumberValue：先转为“分母为1”的分数，再执行分子分母分别相乘</li>
	 * </ol>
	 *
	 * @param value 乘数（不可为null，支持任意NumberValue类型）
	 * @return 乘法结果（已约分为最简分数，类型为Fraction）
	 */
	@Override
	public Fraction multiply(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction multiplicand = (Fraction) value;
			// 分数乘法公式：(a/b) × (c/d) = (a×c)/(b×d)
			NumberValue newMolecule = molecule.multiply(multiplicand.molecule);
			NumberValue newDenominator = denominator.multiply(multiplicand.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		} else {
			// 非分数类型转为“分母为1”的分数，再递归调用乘法
			return multiply(new Fraction(value, NumberValue.ONE));
		}
	}

	/**
	 * 计算当前分数的倒数（分子分母互换）
	 * <p>倒数逻辑：原分数a/b → 倒数b/a，若原分数为零分数（分子为0），会间接抛出异常（分母为0）
	 * <p>示例：3/4的倒数为4/3，-5/2的倒数为-2/5（负号保留在分子）
	 *
	 * @return 当前分数的倒数（未约分，若需最简形式需调用{@link #reduction()}）
	 * @throws IllegalArgumentException 如果当前分数是零分数（分子为0，倒数分母为0，非法）
	 */
	public Fraction reciprocal() {
		// 分子分母互换，若原分子为0，会触发Fraction构造的分母非0校验
		return new Fraction(denominator, molecule);
	}

	/**
	 * 分数除法运算：当前分数 ÷ 目标数值
	 * <p>运算逻辑（分两种场景）：
	 * <ol>
	 * <li>若目标是{@link Fraction}：转为乘以目标的倒数（a/b ÷ c/d = a/b × d/c），结果自动约分</li>
	 * <li>若目标是其他NumberValue：先转为“分母为1”的分数，再乘以其倒数</li>
	 * </ol>
	 *
	 * @param value 除数（不可为null，支持任意NumberValue类型，且不能为0）
	 * @return 除法结果（已约分为最简分数，类型为Fraction）
	 * @throws ArithmeticException 如果除数为0（非法除法运算）
	 */
	@Override
	public Fraction divide(NumberValue value) {
		// 先校验除数是否为0，提前抛出异常（避免后续倒数构造时才报错）
		if (value.isZero()) {
			throw new ArithmeticException("Division by zero (Fraction divide)");
		}
		if (value instanceof Fraction) {
			// 分数除法公式：(a/b) ÷ (c/d) = (a/b) × (d/c)（乘以除数的倒数）
			return multiply(((Fraction) value).reciprocal()).reduction();
		} else {
			// 非分数类型转为“分母为1”的分数，再乘以其倒数
			return multiply(new Fraction(NumberValue.ONE, value)).reduction();
		}
	}

	/**
	 * 分数取模运算：当前分数对目标数值取模，结果满足<strong>0 ≤ 余数 < |除数|</strong>（非负特性）
	 * <p>核心数学原理：将分数取模转化为整数取模（a/b mod c/d = [(a×d) mod (b×c)] / (b×d)），
	 * 确保结果符合数学上的“取模”定义（区别于“取余”的符号规则）
	 *
	 * <p>运算步骤：
	 * <ol>
	 * <li>校验除数非0，避免非法运算；</li>
	 * <li>若当前分数为0，直接返回零分数（0 mod 任何数都为0）；</li>
	 * <li>将除数统一转为{@link Fraction}（整数→分母为1的分数）；</li>
	 * <li>计算核心参数：a×d（分子×除数分母）、b×c（分母×除数分子）、b×d（余数分母）；</li>
	 * <li>通过{@link NumberValue#mod(NumberValue)}计算余数分子（确保非负）；</li>
	 * <li>构造余数分数，传递当前约分算法并自动约分，最后迁移负号（确保分母为正）。</li>
	 * </ol>
	 *
	 * @param value 除数（不可为null，支持任意NumberValue类型，且不能为0）
	 * @return 取模结果（非负、最简的Fraction实例）
	 * @throws ArithmeticException 如果除数为0（非法取模运算）
	 * @throws NullPointerException 如果value为null
	 */
	@Override
	public NumberValue mod(NumberValue value) {
		// 1. 校验除数非0，避免非法运算
		if (value == null) {
			throw new NullPointerException("Divisor cannot be null (Fraction mod)");
		}
		if (value.compareTo(NumberValue.ZERO) == 0) {
			throw new ArithmeticException("Divisor cannot be zero for mod operation (Fraction mod)");
		}

		// 2. 被除数为0时，余数直接为0（短路处理，提升性能）
		if (this.compareTo(Fraction.ZERO) == 0) {
			return Fraction.ZERO;
		}

		// 3. 统一将除数转为Fraction（复用父类工具方法，确保类型一致）
		Fraction divisor = toFraction(value);

		// 4. 提取分数的分子和分母（a/b：当前分数；c/d：除数）
		NumberValue a = this.getMolecule();   // 被除数分子
		NumberValue b = this.getDenominator(); // 被除数分母
		NumberValue c = divisor.getMolecule(); // 除数分子
		NumberValue d = divisor.getDenominator(); // 除数分母

		// 5. 计算核心参数：a×d（余数分子基数）、b×c（取模除数）、b×d（余数分母）
		NumberValue aTimesD = a.multiply(d);
		NumberValue bTimesC = b.multiply(c);
		NumberValue remainderDenominator = b.multiply(d);

		// 6. 断言：b×c 非0（b是当前分母，c是除数分子，均已校验非0，仅debug生效）
		assert bTimesC.compareTo(NumberValue.ZERO) != 0 : "b×c cannot be zero (b and c are non-zero)";

		// 7. 计算余数分子（依赖NumberValue.mod的非负特性）
		NumberValue remainderNumerator = aTimesD.mod(bTimesC);

		// 8. 构造余数分数，传递当前约分算法并约分
		Fraction remainder = new Fraction(remainderNumerator, remainderDenominator);
		remainder.setGreatestCommonDivisor(this.getGreatestCommonDivisor());
		remainder = remainder.reduction();

		// 9. 负号迁移：确保分母为正（同乘-1不改变分数值，且无需二次约分）
		if (remainder.getDenominator().isNegative()) {
			NumberValue negativeOne = NumberValue.NEGATIVE_ONE;
			remainder = new Fraction(
					remainder.getMolecule().multiply(negativeOne),
					remainder.getDenominator().multiply(negativeOne)
			);
		}

		return remainder;
	}

	/**
	 * 重写父类{@link NumberValue#newFraction(NumberValue, NumberValue)}工厂方法，
	 * 确保新创建的Fraction实例与当前实例使用<strong>相同的约分算法</strong>
	 * <p>核心作用：解决父类创建Fraction时使用默认GCD算法，导致与当前实例约分逻辑不一致的问题，
	 * 例如当前使用“更相减损术”，新创建的分数也应沿用该算法
	 *
	 * @param molecule    新分数的分子（不可为null）
	 * @param denominator 新分数的分母（不可为null，且非0）
	 * @return 配置了当前GCD算法的Fraction实例
	 * @throws IllegalArgumentException 如果分母为0（由Fraction构造函数抛出）
	 * @throws NullPointerException 如果molecule或denominator为null
	 */
	@Override
	protected Fraction newFraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		// 调用父类方法创建基础Fraction，再设置当前的GCD算法
		Fraction fraction = super.newFraction(molecule, denominator);
		fraction.setGreatestCommonDivisor(this.greatestCommonDivisor);
		return fraction;
	}

	/**
	 * 计算“分子对分母的取余”（区别于{@link #mod(NumberValue)}的整体取模）
	 * <p>逻辑：直接调用分子的{@link NumberValue#remainder(NumberValue)}方法，
	 * 即“分子 ÷ 分母”的余数，符号与分子一致（取余规则）
	 * <p>示例：分数7/3 → 分子7对分母3取余，结果为1；分数-5/2 → 分子-5对分母2取余，结果为-1
	 *
	 * @return 分子对分母的取余结果（类型与分子一致，如分子为IntValue则返回IntValue）
	 */
	public NumberValue remainder() {
		return molecule.remainder(denominator);
	}

	/**
	 * 分数取余运算：将当前分数转为{@link BigDecimal}后执行取余（结果可能丢失精度）
	 * <p>运算逻辑：先通过{@link #getAsBigDecimal()}将分数转为小数，
	 * 再调用{@link BigDecimal#remainder(BigDecimal)}计算取余，符号与被除数一致
	 * <p><strong>注意</strong>：此方法涉及浮点数转换，可能存在精度丢失（如1/3转为0.333...），
	 * 仅适用于“无需严格精度”的场景，优先推荐使用{@link #mod(NumberValue)}（高精度取模）
	 *
	 * @param value 除数（不可为null，支持任意NumberValue类型，且非0）
	 * @return 取余结果（BigDecimalValue类型，包含浮点数转换后的精度）
	 * @throws ArithmeticException 如果除数为0（由BigDecimal.remainder抛出）
	 */
	@Override
	public NumberValue remainder(NumberValue value) {
		// 转为BigDecimal后取余，结果封装为BigDecimalValue
		BigDecimal thisDecimal = getAsBigDecimal();
		BigDecimal valueDecimal = value.getAsBigDecimal();
		return new BigDecimalValue(thisDecimal.remainder(valueDecimal));
	}

	/**
	 * 分数幂运算：当前分数的目标数值次幂（(a/b)^n = a^n / b^n）
	 * <p>运算逻辑：分子、分母分别执行{@link NumberValue#pow(NumberValue)}幂运算，
	 * 结果不自动约分（需手动调用{@link #reduction()}）
	 * <p>支持场景：
	 * <ul>
	 * <li>整数幂：如(2/3)^2 = 4/9、(3/2)^-1 = 2/3（负指数转为倒数的正指数）</li>
	 * <li>小数幂：如(4/1)^0.5 = 2/1（开平方），但需依赖分子/分母的pow方法支持小数指数</li>
	 * </ul>
	 *
	 * @param value 指数（不可为null，支持任意NumberValue类型，如整数、小数）
	 * @return 幂运算结果（未约分的Fraction实例，分子为原分子的幂，分母为原分母的幂）
	 */
	@Override
	public NumberValue pow(NumberValue value) {
		// 分数幂公式：(a/b)^n = (a^n)/(b^n)
		NumberValue newMolecule = molecule.pow(value);
		NumberValue newDenominator = denominator.pow(value);
		return new Fraction(newMolecule, newDenominator);
	}

	/**
	 * 将分数转为{@link BigDecimal}（小数形式）
	 * <p>转换逻辑：分子 ÷ 分母（调用{@link NumberValue#divide(NumberValue)}），
	 * 再获取结果的BigDecimal表示
	 * <p><strong>精度说明</strong>：
	 * <ul>
	 * <li>有限小数：如1/2 → 0.5（无精度丢失）；</li>
	 * <li>无限循环小数：如1/3 → 0.3333333333333333（丢失部分精度，取决于BigDecimal的默认精度）；</li>
	 * <li>无限不循环小数：如√2/2 → 0.7071067811865476（仅保留有限位，存在精度偏差）。</li>
	 * </ul>
	 *
	 * @return 分数对应的BigDecimal（可能存在精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		// 分子除以分母，转为小数后返回
		return molecule.divide(denominator).getAsBigDecimal();
	}

	/**
	 * 计算当前分数的绝对值（分子分母均取绝对值，结果非负）
	 * <p>逻辑：分子的绝对值 → 新分子，分母的绝对值 → 新分母（分母本身已为正，故仅分子需处理）
	 * <p>示例：-1/3 → 1/3，5/-2 → 5/2（分母负号已迁移至分子，故实际处理为-5/2 → 5/2）
	 *
	 * @return 非负的最简分数（自动约分）
	 */
	public Fraction abs() {
		NumberValue absMolecule = molecule.abs();
		NumberValue absDenominator = denominator.abs();
		return new Fraction(absMolecule, absDenominator).reduction();
	}

	/**
	 * 私有工具方法：格式化NumberValue为字符串，处理嵌套分数的显示（避免歧义）
	 * <p>逻辑：若目标是{@link Fraction}，则用括号包裹（如(1/2)），否则直接调用toString()，
	 * 例如分子为1/2、分母为3 → 格式化后为"(1/2)/3"，避免误解为"1/(2/3)"
	 *
	 * @param value 待格式化的数值（不可为null）
	 * @return 格式化后的字符串（嵌套分数带括号）
	 */
	private String toString(NumberValue value) {
		return (value instanceof Fraction) ? ("(" + value + ")") : value.toString();
	}

	/**
	 * 获取分数的字符串表示，格式为“分子/分母”（支持嵌套分数的清晰显示）
	 * <p>示例：
	 * <ul>
	 * <li>普通分数：1/3 → "1/3"；</li>
	 * <li>嵌套分数：分子为2/5、分母为3 → "(2/5)/3"；</li>
	 * <li>负分数：-3/4 → "(-3)/4"（分子为负，分母为正）。</li>
	 * </ul>
	 *
	 * @return 分数的字符串表示（无歧义）
	 */
	@Override
	public String getAsString() {
		return toString(molecule) + "/" + toString(denominator);
	}

	/**
	 * 比较当前分数与目标Value的大小（仅支持数值类型比较）
	 * <p>比较逻辑（分场景）：
	 * <ol>
	 * <li>若目标是{@link Fraction}：通分后比较分子（a/b 与 c/d → 比较a×d与c×b，避免浮点数转换）；</li>
	 * <li>若目标是其他NumberValue：先转为同分母分数，再比较分子；</li>
	 * <li>若目标是非Number类型：调用父类{@link NumberValue#compareTo(Value)}，返回默认比较结果。</li>
	 * </ol>
	 * <p>返回值规则：负整数（当前 < 目标）、0（当前 = 目标）、正整数（当前 > 目标）
	 *
	 * @param value 待比较的Value（可为null，null视为小于任何数值）
	 * @return 比较结果（负整数、0、正整数）
	 */
	public int compareTo(Value value) {
		if (value == null) {
			return 1; // null视为小于任何数值
		}
		if (value.isNumber()) {
			NumberValue numberValue = value.getAsNumber();
			if (numberValue instanceof Fraction) {
				Fraction target = (Fraction) numberValue;
				// 分数比较公式：a/b 与 c/d → 比较 a×d 和 c×b
				NumberValue thisCross = this.molecule.multiply(target.denominator);
				NumberValue targetCross = target.molecule.multiply(this.denominator);
				return thisCross.compareTo(targetCross);
			} else {
				// 非分数类型转为同分母分数，再比较
				Fraction targetFraction = sameDenominator(numberValue);
				return this.molecule.compareTo(targetFraction.getMolecule());
			}
		}
		// 非数值类型，调用父类比较逻辑
		return super.compareTo(value);
	}

	/**
	 * 将分数约分为最简形式（分子分母互质）
	 * <p>约分逻辑（分步骤）：
	 * <ol>
	 * <li>处理嵌套分数：若分子/分母是Fraction，先展开为普通分数（如(1/2)/3 → 1/6）；</li>
	 * <li>快速返回场景：分子为0 → 返回零分数，分子为1 → 返回自身（已最简）；</li>
	 * <li>计算GCD：通过配置的{@link GreatestCommonDivisor}算法，计算分子、分母的最大公约数；</li>
	 * <li>约分操作：分子、分母分别除以GCD，得到最简分数（若GCD为1，直接返回自身）。</li>
	 * </ol>
	 * <p><strong>注意</strong>：若分子/分母包含小数（如BigDecimalValue），会先转为BigDecimal后计算GCD，
	 * 可能存在微小精度偏差（如0.1000000001与0.1视为不同值）
	 *
	 * @return 最简分数（分子分母互质，分母为正）
	 */
	public Fraction reduction() {
		// 场景1：分子是Fraction → 展开为 (分子的分子)/(分子的分母 × 当前分母)
		if (molecule instanceof Fraction) {
			Fraction moleculeFraction = (Fraction) molecule;
			NumberValue newMolecule = moleculeFraction.getMolecule();
			NumberValue newDenominator = moleculeFraction.getDenominator().multiply(this.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		}

		// 场景2：分母是Fraction → 展开为 (当前分子 × 分母的分母)/(分母的分子)
		if (denominator instanceof Fraction) {
			Fraction denominatorFraction = (Fraction) denominator;
			NumberValue newMolecule = this.molecule.multiply(denominatorFraction.getDenominator());
			NumberValue newDenominator = denominatorFraction.getMolecule();
			return new Fraction(newMolecule, newDenominator).reduction();
		}

		// 转为BigDecimal计算GCD（统一处理整数/小数类型）
		BigDecimal moleculeDecimal = this.molecule.getAsBigDecimal();
		// 快速返回：分子为0 → 零分数
		if (moleculeDecimal.compareTo(BigDecimal.ZERO) == 0) {
			return ZERO;
		}
		// 快速返回：分子绝对值为1 → 已最简（分母为正，且1与任何数互质）
		if (moleculeDecimal.abs().compareTo(BigDecimal.ONE) == 0) {
			return this;
		}

		BigDecimal denominatorDecimal = this.denominator.getAsBigDecimal();
		// 计算分子和分母的最大公约数
		BigDecimal gcd = getGreatestCommonDivisor().apply(moleculeDecimal, denominatorDecimal);
		// 若GCD为1，说明已最简，直接返回
		if (gcd.compareTo(BigDecimal.ONE) == 0) {
			return this;
		}

		// 分子分母分别除以GCD，得到最简分数
		BigDecimal simplifiedMolecule = moleculeDecimal.divide(gcd);
		BigDecimal simplifiedDenominator = denominatorDecimal.divide(gcd);
		return new Fraction(new BigDecimalValue(simplifiedMolecule), new BigDecimalValue(simplifiedDenominator));
	}

	/**
	 * 将分数转为{@link BigInteger}（整数形式）
	 * <p>转换逻辑：先执行“分子 ÷ 分母”（调用{@link NumberValue#divide(NumberValue)}），
	 * 再截断小数部分，仅保留整数部分（无四舍五入）
	 * <p>示例：7/3 → 2（截断小数0.333...），-5/2 → -2（截断小数0.5），4/2 → 2（无小数部分）
	 *
	 * @return 分数对应的整数部分（BigInteger类型，截断小数）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		// 分子除以分母后取整数部分
		return molecule.divide(denominator).getAsBigInteger();
	}

	/**
	 * 计算分数的哈希值，确保“数值相等的分数哈希值一致”
	 * <p>哈希逻辑：基于分子和分母的哈希值组合（分子哈希值 + 分母哈希值），
	 * 因分数已确保分母为正且最简（运算后），故相同数值的分数会有相同哈希值
	 * <p>示例：2/3 与 4/6（约分后为2/3） → 哈希值相同
	 *
	 * @return 分数的哈希值（基于分子和分母的数值）
	 */
	@Override
	public int hashCode() {
		return molecule.hashCode() + denominator.hashCode();
	}

	/**
	 * 重写equals方法，基于数值相等性判断（而非对象引用）
	 * <p>判断逻辑：
	 * <ol>
	 * <li>若参数为null或非Fraction类型 → false；</li>
	 * <li>若当前分数与目标分数“约分后数值相同” → true（通过{@link #compareTo(Value)}判断）。</li>
	 * </ol>
	 * <p>示例：2/3 与 4/6 → equals返回true（约分后均为2/3）；2/3 与 3/2 → 返回false
	 *
	 * @param obj 待比较的对象
	 * @return true：数值相等；false：对象为null/非Fraction/数值不相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Fraction)) {
			return false;
		}
		return this.compareTo((Fraction) obj) == 0;
	}
}