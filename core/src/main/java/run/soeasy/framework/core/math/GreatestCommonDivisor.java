package run.soeasy.framework.core.math;

/**
 * 最大公约数计算器接口，用于计算两个数值的最大公约数（GCD），继承自{@link Calculator}接口。
 * 该接口定义了计算最大公约数的标准方法，支持多种数值类型和算法实现， 适用于数学计算、分数约分、密码学等需要最大公约数的场景。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>多算法支持：提供除法算法（欧几里得算法）和减法算法（更相减损术）两种实现</li>
 * <li>多类型支持：支持int、long、BigInteger、BigDecimal等多种数值类型</li>
 * <li>函数式接口：作为{@link Calculator}的子接口，支持表达式计算集成</li>
 * <li>高精度计算：对BigInteger和BigDecimal提供精确的最大公约数计算</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>分数约分：将分数约分为最简形式</li>
 * <li>密码学：RSA算法等需要计算最大公约数的密码学场景</li>
 * <li>数学计算：几何、数论等数学问题中的公约数计算</li>
 * <li>表达式求值：作为表达式中的一个操作符使用</li>
 * </ul>
 *
 * <p>
 * 算法说明：
 * <ul>
 * <li>{@link #DIVISION_ALGORITHM}：基于欧几里得算法（辗转相除法），时间复杂度O(log(min(m,n)))</li>
 * <li>{@link #DEROGATION_METHOD}：基于更相减损术，适用于处理大数且避免取模运算的场景</li>
 * </ul>
 *
 * <p>
 * 示例用法：
 * 
 * <pre class="code">
 * // 使用除法算法计算整数最大公约数
 * GreatestCommonDivisor gcd = GreatestCommonDivisor.DIVISION_ALGORITHM;
 * int result1 = gcd.gcd(24, 18); // 6
 * 
 * // 计算BigInteger的最大公约数
 * BigInteger m = new BigInteger("123456789");
 * BigInteger n = new BigInteger("987654321");
 * BigInteger result2 = gcd.gcd(m, n); // 9
 * 
 * // 作为计算器使用
 * NumberValue a = new IntValue(36);
 * NumberValue b = new IntValue(24);
 * NumberValue gcdResult = gcd.apply(a, b); // IntValue(12)
 * </pre>
 *
 * @see Calculator
 * @see DivisionAlgorithm
 * @see DerogationMethod
 */
@FunctionalInterface
public interface GreatestCommonDivisor extends Calculator {
	/** 除法算法（欧几里得算法）实现的最大公约数计算器 */
	public static final DivisionAlgorithm DIVISION_ALGORITHM = new DivisionAlgorithm();
	/** 减法算法（更相减损术）实现的最大公约数计算器 */
	public static final DerogationMethod DEROGATION_METHOD = new DerogationMethod();

	/** 最大公约数运算符标识 */
	static final String GCD_OPERATOR = "GCD";

	/**
	 * 获取运算符字符串，固定返回"GCD"。
	 *
	 * @return "GCD"字符串
	 */
	@Override
	default String getOperator() {
		return GCD_OPERATOR;
	}
}