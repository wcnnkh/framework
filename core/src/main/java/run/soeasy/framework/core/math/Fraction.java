package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.domain.Value;

/**
 * 高精度分数实现类，继承自{@link NumberValue}，以“分子/分母”形式表示有理数，
 * 彻底解决浮点数精度丢失问题（如{@code 0.1 + 0.2 ≠ 0.3}），所有运算基于整数/高精度数操作，
 * 是框架中跨类型（整数、小数、大数）精确 数值计算的核心载体。
 * 
 * <p><strong>设计核心（三大核心约束）</strong>：
 * <ul>
 * <li><strong>分母恒正</strong>：通过“负号迁移”逻辑，将分母的负号转移至分子（如{@code 3/-2 → -3/2}），
 * 避免后续运算中正负号混淆（如取模、约分的符号判断）；</li>
 * <li><strong>自动约分可控</strong>：核心运算（加、减、乘、除）结果自动约分为最简形式，
 * 非核心运算（幂运算）需手动调用{@link #reduction()}，平衡精度与性能；</li>
 * <li><strong>类型无缝兼容</strong>：支持与任意{@link NumberValue}子类（{@link IntValue}、{@link BigDecimalValue}等）运算，
 * 非分数类型自动转为“分母为1”的分数（如{@code 5 → 5/1}），统一运算逻辑。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>无精度丢失运算</strong>：所有操作基于分子、分母的整数/高精度计算，不涉及浮点数转换，
 * 支持无限循环小数（如{@code 1/3}）、有限小数（如{@code 1/2}）的精确表示；</li>
 * <li><strong>灵活约分策略</strong>：通过{@link #greatestCommonDivisor}配置GCD算法（默认欧几里得算法），
 * 支持自定义（如更相减损术），适配不同性能需求（如大数值场景更相减损术可能更优）；</li>
 * <li><strong>严格异常控制</strong>：
 *   - 分母为0（构造时）、零分数求倒数（分子为0）→ 抛{@link IllegalArgumentException}；
 *   - 除数为0（除法、取模）→ 抛{@link ArithmeticException}；
 *   避免运行时隐藏错误；</li>
 * <li><strong>完整类型转换</strong>：
 *   - 转{@link BigDecimal}：分子÷分母（可能存在精度丢失，如{@code 1/3 → 0.3333333333333333}）；
 *   - 转{@link BigInteger}：截断小数部分（如{@code 7/3 → 2}，{@code -5/2 → -2}）；
 *   - 转字符串：格式为“分子/分母”（嵌套分数带括号，如{@code (1/2)/3}）；</li>
 * <li><strong>清晰运算语义</strong>：
 *   - {@link #mod(NumberValue)}：结果非负（符合数学取模定义，如{@code -5/2 mod 3 → 1/2}）；
 *   - {@link #remainder(NumberValue)}：结果符号与分子一致（如{@code -5/2 remainder 3 → -1/2}）；
 *   明确区分“取模”与“取余”。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融计算：货币拆分（如100元分3人，每人100/3元）、利息计算（如年利率3.5% → 7/200），避免分厘级误差；</li>
 * <li>科学实验：浓度计算（如10g溶质溶于90g溶剂 → 1/10浓度）、工程测量（如3米长材料分7段 → 3/7米/段）；</li>
 * <li>游戏开发：概率公式（如暴击率15% → 3/20）、资源分配（如5个道具分给8个玩家 → 5/8个/人，累计后无损耗）；</li>
 * <li>数据对账：需精确比较的场景（如账单金额1/3元与0.33333333元，避免因精度丢失导致对账失败）。</li>
 * </ul>
 *
 * <h3>使用示例（含关键场景）</h3>
 * <pre class="code">
 * // 1. 构造分数（多种场景）
 * Fraction f1 = new Fraction(new IntValue(1), new IntValue(3)); // 1/3（基础整数分子分母）
 * Fraction f2 = new Fraction(new BigDecimal("0.1")); // 0.1 → 1/10（小数自动转分数）
 * Fraction f3 = new Fraction(new BigIntegerValue("7"), new IntValue(2)); // 7/2（超大整数分子）
 * Fraction f4 = new Fraction(new IntValue(3), new IntValue(-4)); // 负号迁移 → -3/4（分母恒正）
 *
 * // 2. 基础运算（自动约分）
 * Fraction sum = f1.add(f2); // 1/3 + 1/10 = (10+3)/30 = 13/30（自动约分，13与30互质）
 * Fraction diff = f3.subtract(f1); // 7/2 - 1/3 = (21-2)/6 = 19/6（自动约分）
 * Fraction product = sum.multiply(diff); // 13/30 × 19/6 = 247/180（自动约分，247=13×19，180=2²×3²，互质）
 * Fraction quotient = product.divide(f4); // 247/180 ÷ (-3/4) = 247/180 × (-4/3) = -988/540 → 约分后-247/135
 *
 * // 3. 取模与取余对比（关键区别）
 * NumberValue modResult = f3.mod(new IntValue(2)); // 7/2 mod 2（=2/1）→ (7×1)mod(2×2)=7mod4=3 → 3/2（非负）
 * NumberValue remResult = f3.remainder(new IntValue(2)); // 7/2 remainder 2 → 7%2=1 → 1/2（符号与分子一致）
 * NumberValue negMod = new Fraction(new IntValue(-5), new IntValue(2)).mod(new IntValue(3)); // -5/2 mod3 → 1/2（非负）
 *
 * // 4. 幂运算（需手动约分）
 * Fraction powResult = (Fraction) f2.pow(new IntValue(2)); // (1/10)² = 1/100（未约分，已最简）
 * Fraction powResult2 = (Fraction) new Fraction(new IntValue(4), new IntValue(2)).pow(new IntValue(2)); // (4/2)²=16/4 → 需手动约分
 * Fraction simplifiedPow = (Fraction) powResult2.reduction(); // 16/4 → 4/1
 *
 * // 5. 倒数（注意零分数不可用）
 * Fraction reciprocalF1 = f1.reciprocal(); // 1/3 → 3/1
 * // Fraction reciprocalZero = Fraction.ZERO.reciprocal(); // 抛IllegalArgumentException（分子为0，倒数分母为0）
 *
 * // 6. 类型转换
 * BigDecimal bd = quotient.getAsBigDecimal(); // -247/135 ≈ -1.8296296296296295（存在精度丢失）
 * BigInteger bi = quotient.getAsBigInteger(); // -247/135 → 截断小数 → -1
 * String str = quotient.getAsString(); // "-247/135"（清晰格式）
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li>单例常量{@link #ZERO}：不可调用{@link #reciprocal()}（抛异常），不可修改其{@link #greatestCommonDivisor}（影响全局）；</li>
 * <li>幂运算{@link #pow(NumberValue)}：结果不自动约分，需手动调用{@link #reduction()}，如{@code (4/2)^2}需手动处理；</li>
 * <li>嵌套分数：分子/分母为{@link Fraction}时，{@link #reduction()}会自动展开，如{@code (1/2)/3 → 1/6}，无需手动拆解；</li>
 * <li>BigDecimal转换精度：{@link #getAsBigDecimal()}依赖默认精度（约15-17位有效数字），若需更高精度，需自行实现“分子÷分母”并指定精度（如{@code new BigDecimal(molecule).divide(new BigDecimal(denominator), 50, RoundingMode.HALF_UP)}）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see NumberValue 父类（定义数值运算与转换的基础契约）
 * @see GreatestCommonDivisor GCD算法接口（支撑约分逻辑，默认欧几里得算法）
 * @see BigDecimalValue 高精度小数类（可无缝转为分数，如{@code 0.25 → 1/4}）
 * @see BigIntegerValue 超大整数类（可作为分子/分母，支持超大数值分数）
 */
@Getter
@Setter
public class Fraction extends NumberValue {
	private static final long serialVersionUID = 1L;

	/**
	 * 零分数单例常量（分子为{@link BigIntegerValue#ZERO}，分母为{@link BigIntegerValue#ONE}）
	 * <p>用途：作为“数值为0”的基准（如比较运算、判断结果是否为0），避免重复创建零分数实例
	 * <p><strong>禁忌</strong>：不可调用{@link #reciprocal()}（会抛{@link IllegalArgumentException}），不可修改其GCD算法
	 */
	public static final Fraction ZERO = new Fraction(BigIntegerValue.ZERO, BigIntegerValue.ONE);

	/**
	 * 分数的分子（不可变）
	 * <p>特性：支持任意{@link NumberValue}类型（整数、小数、超大整数），负号统一存储于此（分母恒正）
	 */
	private final NumberValue molecule;
	/**
	 * 分数的分母（不可变，恒正）
	 * <p>特性：通过构造时的“负号迁移”确保非负，避免后续运算中符号判断复杂（如取模、约分）
	 */
	private final NumberValue denominator;

	/**
	 * 最大公约数（GCD）计算器（默认：{@link GreatestCommonDivisor#DIVISION_ALGORITHM}（欧几里得算法））
	 * <p>用途：控制{@link #reduction()}的约分逻辑，可替换为更相减损术等其他算法，适配不同数值场景
	 */
	@NonNull
	private GreatestCommonDivisor greatestCommonDivisor = GreatestCommonDivisor.DIVISION_ALGORITHM;

	/**
	 * 从{@link BigDecimal}创建分数（小数→分数的自动转换）
	 * <p>转换逻辑：
	 * 1. 获取BigDecimal的小数位数{@code scale}（如0.1的scale=1，0.001的scale=3）；
	 * 2. 分子 = 小数去掉小数点后的整数（如0.1→1，0.001→1，1.25→125）；
	 * 3. 分母 = 10^scale（如scale=1→10，scale=3→1000，scale=2→100）；
	 * 4. 最终分数未约分（后续可通过{@link #reduction()}优化，如0.25→25/100→1/4）。
	 *
	 * @param bigDecimal 待转换的小数（不可为null，支持任意scale，如{@code new BigDecimal("0.100")}（scale=3））
	 */
	public Fraction(BigDecimal bigDecimal) {
		this(new BigDecimalValue(bigDecimal), new BigDecimalValue(BigDecimal.ONE));
	}

	/**
	 * 核心构造：通过分子和分母创建分数（所有构造的最终入口）
	 * <p>核心逻辑（两步校验与处理）：
	 * 1. 分母非0校验：若分母为0，抛{@link IllegalArgumentException}（非法分数）；
	 * 2. 负号迁移：若分母为负，将负号转移至分子（分子×-1，分母×-1），确保分母恒正（如分子3、分母-2 → 分子-3、分母2）。
	 *
	 * @param molecule    分数的分子（不可为null，支持任意{@link NumberValue}类型，如{@link IntValue}、{@link BigIntegerValue}）
	 * @param denominator 分数的分母（不可为null，支持任意{@link NumberValue}类型，且不能为0）
	 * @throws IllegalArgumentException 若分母数值为0（{@code denominator.compareTo(NumberValue.ZERO) == 0}）
	 * @throws NullPointerException     若molecule或denominator为null
	 */
	public Fraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		// 第一步：校验分母非0（非法分数拦截）
		if (denominator.compareTo(NumberValue.ZERO) == 0) {
			throw new IllegalArgumentException("Denominator cannot be zero (illegal fraction, e.g., a/0)");
		}
		// 第二步：负号迁移（确保分母恒正）
		if (denominator.isNegative()) {
			this.molecule = molecule.multiply(NumberValue.NEGATIVE_ONE); // 分子承接负号
			this.denominator = denominator.abs(); // 分母取绝对值
		} else {
			this.molecule = molecule;
			this.denominator = denominator;
		}
	}

	/**
	 * 将目标数值转为“与当前分数同分母”的分数（用于加减法通分）
	 * <p>转换逻辑：
	 * 新分子 = 目标数值 × 当前分母；
	 * 新分母 = 当前分母；
	 * （示例：当前分数1/3，目标数值2（=2/1）→ 新分子2×3=6，新分母3 → 6/3）
	 * <p>注意：转换后的分数未约分（后续加减法会统一约分，避免重复计算）。
	 *
	 * @param value 待转换的数值（不可为null，支持任意{@link NumberValue}类型，如整数、小数）
	 * @return 同分母分数（未约分，分母与当前分数一致）
	 */
	public Fraction sameDenominator(NumberValue value) {
		return new Fraction(value.multiply(this.denominator), denominator);
	}

	/**
	 * 分数加法：当前分数 + 目标数值（结果自动约分）
	 * <p>运算逻辑（分场景）：
	 * 1. 目标为{@link Fraction}（c/d）：
	 *    公式：a/b + c/d = (a×d + c×b) / (b×d)；
	 *    示例：1/3 + 1/10 = (1×10 + 1×3)/(3×10) = 13/30（自动约分，13与30互质）；
	 * 2. 目标为其他{@link NumberValue}（如5→5/1）：
	 *    先通过{@link #sameDenominator(NumberValue)}转为同分母分数，再执行上述加法。
	 *
	 * @param value 加数（不可为null，支持任意{@link NumberValue}类型）
	 * @return 加法结果（已约分为最简分数，分母恒正）
	 */
	@Override
	public Fraction add(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction summand = (Fraction) value;
			// 分数加法公式：(a/b) + (c/d) = (a×d + c×b)/(b×d)
			NumberValue newMolecule = molecule.multiply(summand.denominator)
					.add(summand.molecule.multiply(denominator));
			NumberValue newDenominator = denominator.multiply(summand.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		} else {
			// 非分数类型→同分母分数→递归加法
			return add(sameDenominator(value));
		}
	}

	/**
	 * 分数减法：当前分数 - 目标数值（结果自动约分）
	 * <p>运算逻辑（分场景）：
	 * 1. 目标为{@link Fraction}（c/d）：
	 *    公式：a/b - c/d = (a×d - c×b) / (b×d)；
	 *    示例：7/2 - 1/3 = (7×3 - 1×2)/(2×3) = 19/6（自动约分）；
	 * 2. 目标为其他{@link NumberValue}（如2→2/1）：
	 *    先通过{@link #sameDenominator(NumberValue)}转为同分母分数，再执行上述减法。
	 *
	 * @param value 减数（不可为null，支持任意{@link NumberValue}类型）
	 * @return 减法结果（已约分为最简分数，分母恒正）
	 */
	@Override
	public Fraction subtract(NumberValue value) {
		if (value instanceof Fraction) {
			Fraction minuend = (Fraction) value;
			// 分数减法公式：(a/b) - (c/d) = (a×d - c×b)/(b×d)
			NumberValue newMolecule = molecule.multiply(minuend.denominator)
					.subtract(minuend.molecule.multiply(denominator));
			NumberValue newDenominator = denominator.multiply(minuend.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		} else {
			// 非分数类型→同分母分数→递归减法
			return subtract(sameDenominator(value));
		}
	}

	/**
	 * 分数乘法：当前分数 × 目标数值（结果自动约分）
	 * <p>运算逻辑（分场景）：
	 * 1. 目标为{@link Fraction}（c/d）：
	 *    公式：a/b × c/d = (a×c)/(b×d)；
	 *    示例：13/30 × 19/6 = 247/180（自动约分，247与180互质）；
	 * 2. 目标为其他{@link NumberValue}（如5→5/1）：
	 *    先转为“分母为1”的分数（new Fraction(value, NumberValue.ONE)），再执行上述乘法。
	 *
	 * @param value 乘数（不可为null，支持任意{@link NumberValue}类型）
	 * @return 乘法结果（已约分为最简分数，分母恒正）
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
			// 非分数类型→分母为1的分数→递归乘法
			return multiply(new Fraction(value, NumberValue.ONE));
		}
	}

	/**
	 * 计算当前分数的倒数（分子分母互换）
	 * <p>倒数逻辑：原分数a/b → 倒数b/a（分母恒正，负号保留在分子）；
	 * 示例：3/4→4/3，-5/2→-2/5，1/10→10/1。
	 * <p>异常触发：若当前分数为{@link #ZERO}（分子为0），则倒数的分母为0，抛{@link IllegalArgumentException}。
	 *
	 * @return 倒数分数（未约分，分母恒正）
	 * @throws IllegalArgumentException 若当前分数为{@link #ZERO}（分子为0，倒数分母为0）
	 */
	public Fraction reciprocal() {
		// 分子分母互换，触发构造的分母非0校验（若原分子为0，会抛异常）
		return new Fraction(denominator, molecule);
	}

	/**
	 * 分数除法：当前分数 ÷ 目标数值（结果自动约分）
	 * <p>运算逻辑（分场景）：
	 * 1. 前置校验：若目标为0，抛{@link ArithmeticException}（除数为0）；
	 * 2. 目标为{@link Fraction}（c/d）：
	 *    转为乘法：a/b ÷ c/d = a/b × d/c（乘以除数的倒数）；
	 *    示例：247/180 ÷ (-3/4) = 247/180 × (-4/3) = -988/540 → 约分后-247/135；
	 * 3. 目标为其他{@link NumberValue}（如2→2/1）：
	 *    先转为“分母为1”的分数，再乘以其倒数（如2的倒数为1/2）。
	 *
	 * @param value 除数（不可为null，支持任意{@link NumberValue}类型，且不能为0）
	 * @return 除法结果（已约分为最简分数，分母恒正）
	 * @throws ArithmeticException  若目标数值为0（除数为0）
	 * @throws NullPointerException 若value为null
	 */
	@Override
	public Fraction divide(NumberValue value) {
		// 前置校验：除数为0拦截
		if (value == null) {
			throw new NullPointerException("Divisor cannot be null (Fraction divide)");
		}
		if (value.isZero()) {
			throw new ArithmeticException("Division by zero (Fraction divide, e.g., a/b ÷ 0)");
		}

		if (value instanceof Fraction) {
			// 分数除法→乘以除数的倒数
			return multiply(((Fraction) value).reciprocal()).reduction();
		} else {
			// 非分数类型→分母为1的分数→乘以其倒数
			return multiply(new Fraction(NumberValue.ONE, value)).reduction();
		}
	}

	/**
	 * 分数取模：当前分数对目标数值取模（结果非负，符合数学取模定义）
	 * <p>核心原理：将分数取模转化为整数取模（避免浮点数误差）：
	 * 公式：(a/b) mod (c/d) = [(a×d) mod (b×c)] / (b×d)；
	 * 示例：7/3 mod 2（=2/1）→ (7×1) mod (3×2) =7 mod6=1 → 分母3×1=3 → 结果1/3（非负）。
	 * <p>运算步骤：
	 * 1. 校验除数非0；
	 * 2. 目标数值转为{@link Fraction}（如2→2/1）；
	 * 3. 计算分子部分：(a×d) mod (b×c)（确保非负）；
	 * 4. 计算分母部分：b×d；
	 * 5. 构造分数并约分，确保分母恒正。
	 *
	 * @param value 除数（不可为null，支持任意{@link NumberValue}类型，且不能为0）
	 * @return 取模结果（非负、最简分数，分母恒正）
	 * @throws ArithmeticException  若除数为0
	 * @throws NullPointerException 若value为null
	 */
	@Override
	public NumberValue mod(NumberValue value) {
		// 步骤1：校验除数非0
		if (value == null) {
			throw new NullPointerException("Divisor cannot be null (Fraction mod)");
		}
		if (value.isZero()) {
			throw new ArithmeticException("Divisor cannot be zero for mod (Fraction mod)");
		}

		// 步骤2：被除数为0→直接返回0
		if (this.compareTo(Fraction.ZERO) == 0) {
			return Fraction.ZERO;
		}

		// 步骤3：统一除数为Fraction（如2→2/1）
		Fraction divisor = toFraction(value);

		// 步骤4：提取参数（a/b：当前分数；c/d：除数）
		NumberValue a = this.molecule;   // 被除数分子
		NumberValue b = this.denominator;// 被除数分母
		NumberValue c = divisor.molecule;// 除数分子
		NumberValue d = divisor.denominator;// 除数分母

		// 步骤5：计算核心参数（分子基数、取模除数、结果分母）
		NumberValue aTimesD = a.multiply(d);    // a×d（分子基数）
		NumberValue bTimesC = b.multiply(c);    // b×c（取模除数）
		NumberValue resultDenominator = b.multiply(d); // b×d（结果分母）

		// 步骤6：计算非负余数分子（依赖NumberValue.mod的非负特性）
		NumberValue resultMolecule = aTimesD.mod(bTimesC);

		// 步骤7：构造结果并约分（沿用当前GCD算法）
		Fraction result = new Fraction(resultMolecule, resultDenominator);
		result.setGreatestCommonDivisor(this.greatestCommonDivisor);
		result = result.reduction();

		// 步骤8：确保分母恒正（兜底处理，避免约分后意外出现负分母）
		if (result.denominator.isNegative()) {
			NumberValue negOne = NumberValue.NEGATIVE_ONE;
			result = new Fraction(result.molecule.multiply(negOne), result.denominator.multiply(negOne));
		}

		return result;
	}

	/**
	 * 重写父类工厂方法：确保新创建的{@link Fraction}沿用当前的GCD算法
	 * <p>核心作用：解决父类默认创建的Fraction使用“默认GCD算法”（欧几里得），与当前实例算法不一致的问题，
	 * 例如当前用“更相减损术”，新创建的分数也会沿用，保证约分逻辑统一。
	 *
	 * @param molecule    新分数的分子（不可为null）
	 * @param denominator 新分数的分母（不可为null，且非0）
	 * @return 配置了当前GCD算法的Fraction实例
	 * @throws IllegalArgumentException 若分母为0（由Fraction构造抛出）
	 */
	@Override
	protected Fraction newFraction(@NonNull NumberValue molecule, @NonNull NumberValue denominator) {
		Fraction fraction = super.newFraction(molecule, denominator);
		fraction.setGreatestCommonDivisor(this.greatestCommonDivisor); // 继承当前GCD算法
		return fraction;
	}

	/**
	 * 分子对分母的取余（结果符号与分子一致，区别于{@link #mod(NumberValue)}）
	 * <p>逻辑：直接调用分子的{@link NumberValue#remainder(NumberValue)}，即“分子 ÷ 分母”的取余；
	 * 示例：7/3→7 remainder 3=1，-5/2→-5 remainder 2=-1，4/2→4 remainder 2=0。
	 * <p>用途：需保留分子符号的场景（如负数分数的余数计算），非数学意义上的取模。
	 *
	 * @return 取余结果（类型与分子一致，如分子为{@link IntValue}则返回{@link IntValue}）
	 */
	public NumberValue remainder() {
		return molecule.remainder(denominator);
	}

	/**
	 * 分数取余：基于{@link BigDecimal}的取余（可能丢失精度，慎用）
	 * <p>逻辑：先将分数转为{@link BigDecimal}（分子÷分母），再调用{@link BigDecimal#remainder(BigDecimal)}；
	 * 示例：1/3（≈0.3333）remainder 0.2 → 0.13333333333333333（存在精度丢失）。
	 * <p><strong>警告</strong>：因涉及浮点数转换，结果可能偏离精确值，优先推荐{@link #mod(NumberValue)}或{@link #remainder()}。
	 *
	 * @param value 除数（不可为null，支持任意{@link NumberValue}类型，且非0）
	 * @return 取余结果（{@link BigDecimalValue}类型，可能含精度丢失）
	 * @throws ArithmeticException 若除数为0
	 */
	@Override
	public NumberValue remainder(NumberValue value) {
		if (value.isZero()) {
			throw new ArithmeticException("Division by zero (Fraction remainder)");
		}
		// 转为BigDecimal后取余（精度依赖默认转换）
		BigDecimal thisBd = getAsBigDecimal();
		BigDecimal valueBd = value.getAsBigDecimal();
		return new BigDecimalValue(thisBd.remainder(valueBd));
	}

	/**
	 * 分数幂运算：当前分数的目标数值次幂（结果未自动约分）
	 * <p>运算逻辑：公式(a/b)^n = (a^n)/(b^n)；
	 * 示例：
	 * - 整数幂：(2/3)^2 = 4/9，(3/2)^-1 = 2/3（负指数→倒数的正指数）；
	 * - 小数幂：(4/1)^0.5 = 2/1（开平方，依赖分子/分母的pow方法支持）。
	 * <p>注意：结果未约分，需手动调用{@link #reduction()}（如(4/2)^2=16/4→需转为4/1）。
	 *
	 * @param value 指数（不可为null，支持任意{@link NumberValue}类型，如整数、小数）
	 * @return 幂运算结果（未约分，分母恒正）
	 */
	@Override
	public NumberValue pow(NumberValue value) {
		// 分数幂公式：(a/b)^n = (a^n)/(b^n)
		NumberValue newMolecule = molecule.pow(value);
		NumberValue newDenominator = denominator.pow(value);
		return new Fraction(newMolecule, newDenominator);
	}

	/**
	 * 将分数转为{@link BigDecimal}（可能丢失精度）
	 * <p>转换逻辑：分子 ÷ 分母（调用{@link NumberValue#divide(NumberValue)}）；
	 * 精度说明：
	 * - 有限小数（如1/2=0.5）→ 无精度丢失；
	 * - 无限循环小数（如1/3≈0.3333333333333333）→ 丢失部分精度；
	 * - 无限不循环小数（如√2/2≈0.7071067811865476）→ 仅保留默认精度（15-17位）。
	 *
	 * @return 分数对应的{@link BigDecimal}（可能含精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		// 分子÷分母，转为小数（精度依赖divide方法的默认实现）
		return molecule.divide(denominator).getAsBigDecimal();
	}

	/**
	 * 计算当前分数的绝对值（结果非负，已约分）
	 * <p>逻辑：分子取绝对值，分母取绝对值（分母已恒正，故仅分子处理），结果自动约分；
	 * 示例：-1/3→1/3，5/-2（已转为-5/2）→5/2，-7/6→7/6。
	 *
	 * @return 非负的最简分数（分母恒正）
	 */
	public Fraction abs() {
		NumberValue absMolecule = molecule.abs();
		NumberValue absDenominator = denominator.abs();
		return new Fraction(absMolecule, absDenominator).reduction();
	}

	/**
	 * 工具方法：格式化数值为字符串（处理嵌套分数，避免歧义）
	 * <p>逻辑：若数值是{@link Fraction}，用括号包裹（如(1/2)），否则直接调用toString()；
	 * 示例：分子为1/2、分母为3 → 格式化后为"(1/2)/3"（避免误解为1/(2/3)）。
	 *
	 * @param value 待格式化的数值（不可为null）
	 * @return 格式化字符串（嵌套分数带括号）
	 */
	private String toString(NumberValue value) {
		return (value instanceof Fraction) ? ("(" + value + ")") : value.toString();
	}

	/**
	 * 分数的字符串表示（格式：“分子/分母”，嵌套分数带括号）
	 * <p>示例：
	 * - 普通分数：1/3 → "1/3"，-5/2 → "-5/2"；
	 * - 嵌套分数：(1/2)/3 → "(1/2)/3"，2/(3/4) → "2/(3/4)"。
	 *
	 * @return 分数的字符串形式（无歧义）
	 */
	@Override
	public String getAsString() {
		return toString(molecule) + "/" + toString(denominator);
	}

	/**
	 * 比较当前分数与目标数值的大小（基于数值相等性，非引用）
	 * <p>比较逻辑（分场景）：
	 * 1. 目标为null → 返回1（null小于任何数值）；
	 * 2. 目标为数值类型（{@link Value#isNumber()}）：
	 *    - 目标为{@link Fraction}：通分后比较分子（a/b 与 c/d → 比较a×d与c×b）；
	 *    - 目标为其他类型：转为同分母分数，比较分子；
	 * 3. 目标为非数值类型 → 调用父类比较（按类名哈希排序）。
	 * <p>示例：1/3（≈0.333）&gt; 0.3 → true，2/3 == 4/6 → true。
	 *
	 * @param value 待比较的{@link Value}（可为null）
	 * @return 比较结果：负整数（当前&lt;目标）、0（当前=目标）、正整数（当前&gt;目标）
	 */
	public int compareTo(Value value) {
		if (value == null) {
			return 1;
		}
		if (value.isNumber()) {
			NumberValue numberValue = value.getAsNumber();
			if (numberValue instanceof Fraction) {
				Fraction target = (Fraction) numberValue;
				// 分数比较：a/b 与 c/d → 比较 a×d 和 c×b（避免浮点数转换）
				NumberValue thisCross = this.molecule.multiply(target.denominator);
				NumberValue targetCross = target.molecule.multiply(this.denominator);
				return thisCross.compareTo(targetCross);
			} else {
				// 非分数类型→同分母分数→比较分子
				Fraction targetFraction = sameDenominator(numberValue);
				return this.molecule.compareTo(targetFraction.getMolecule());
			}
		}
		// 非数值类型→父类逻辑
		return super.compareTo(value);
	}

	/**
	 * 将分数约分为最简形式（分子分母互质）
	 * <p>约分步骤：
	 * 1. 展开嵌套分数：若分子/分母是{@link Fraction}，先转为普通分数（如(1/2)/3 → 1/6）；
	 * 2. 快速返回：分子为0→返回{@link #ZERO}，分子绝对值为1→返回自身（已最简）；
	 * 3. 计算GCD：用配置的{@link #greatestCommonDivisor}算法，计算分子、分母的最大公约数；
	 * 4. 约分操作：分子÷GCD，分母÷GCD（若GCD为1，直接返回自身）。
	 * <p>示例：4/6 → GCD(4,6)=2 → 4÷2=2，6÷2=3 → 2/3。
	 *
	 * @return 最简分数（分子分母互质，分母恒正）
	 */
	public Fraction reduction() {
		// 步骤1：展开嵌套分数（分子是Fraction）
		if (molecule instanceof Fraction) {
			Fraction moleculeFraction = (Fraction) molecule;
			NumberValue newMolecule = moleculeFraction.getMolecule();
			NumberValue newDenominator = moleculeFraction.getDenominator().multiply(this.denominator);
			return new Fraction(newMolecule, newDenominator).reduction();
		}

		// 步骤1：展开嵌套分数（分母是Fraction）
		if (denominator instanceof Fraction) {
			Fraction denominatorFraction = (Fraction) denominator;
			NumberValue newMolecule = this.molecule.multiply(denominatorFraction.getDenominator());
			NumberValue newDenominator = denominatorFraction.getMolecule();
			return new Fraction(newMolecule, newDenominator).reduction();
		}

		// 步骤2：转为BigDecimal（统一处理整数/小数类型）
		BigDecimal moleculeBd = this.molecule.getAsBigDecimal();
		BigDecimal denominatorBd = this.denominator.getAsBigDecimal();

		// 步骤3：快速返回场景1：分子为0→零分数
		if (moleculeBd.compareTo(BigDecimal.ZERO) == 0) {
			return ZERO;
		}

		// 步骤3：快速返回场景2：分子绝对值为1→已最简（1与任何数互质）
		if (moleculeBd.abs().compareTo(BigDecimal.ONE) == 0) {
			return this;
		}

		// 步骤4：计算GCD（用配置的算法）
		BigDecimal gcd = getGreatestCommonDivisor().apply(moleculeBd, denominatorBd);

		// 步骤5：GCD为1→已最简
		if (gcd.compareTo(BigDecimal.ONE) == 0) {
			return this;
		}

		// 步骤6：约分（分子÷GCD，分母÷GCD）
		BigDecimal simplifiedMolecule = moleculeBd.divide(gcd);
		BigDecimal simplifiedDenominator = denominatorBd.divide(gcd);
		return new Fraction(new BigDecimalValue(simplifiedMolecule), new BigDecimalValue(simplifiedDenominator));
	}

	/**
	 * 将分数转为{@link BigInteger}（截断小数部分，非四舍五入）
	 * <p>转换逻辑：先执行“分子 ÷ 分母”（调用{@link NumberValue#divide(NumberValue)}），
	 * 再取结果的整数部分（截断小数）；
	 * 示例：7/3→2，-5/2→-2，4/2→2，1/3→0。
	 *
	 * @return 分数对应的整数部分（{@link BigInteger}类型）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		// 分子÷分母后取整数部分（截断小数）
		return molecule.divide(denominator).getAsBigInteger();
	}

	/**
	 * 哈希值计算（基于约分后的分子和分母，符合equals-hashCode契约）
	 * <p>逻辑：分子哈希值 + 分母哈希值（因约分后分子分母唯一，故相同数值的分数哈希一致）；
	 * 示例：2/3 与 4/6（约分后均为2/3）→ 哈希值相同。
	 *
	 * @return 分数的哈希值（与数值强关联）
	 */
	@Override
	public int hashCode() {
		// 基于约分后的分子分母计算（确保相同数值哈希一致）
		Fraction simplified = this.reduction();
		return simplified.molecule.hashCode() + simplified.denominator.hashCode();
	}

	/**
	 * equals判断（基于数值相等性，非引用）
	 * <p>判断逻辑：
	 * 1. 目标为null或非{@link Fraction}类型 → false；
	 * 2. 目标为{@link Fraction}类型 → 比较约分后的数值（通过{@link #compareTo(Value)} == 0）；
	 * <p>示例：2/3 == 4/6 → true，-1/2 == 1/-2 → true（均约分后为-1/2）。
	 *
	 * @param obj 待比较的对象（可为null）
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
		// 基于约分后的数值比较（确保相等性）
		return this.compareTo((Fraction) obj) == 0;
	}
}