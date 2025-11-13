package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 高精度十进制数值实现类，封装{@link BigDecimal}解决浮点数（float/double）精度丢失问题，
 * 支持高精度数值的存储、转换与比较，适用于财务计算、科学计算等需精确十进制表示的场景。
 *
 * <p>核心特性：
 * <ul>
 * <li>不可变设计：基于{@link BigDecimal}的不可变性，线程安全，所有操作返回新实例，原实例状态不变；</li>
 * <li>无精度丢失：完全复用{@link BigDecimal}的十进制精确表示，避免二进制浮点误差（如0.1+0.2≠0.3）；</li>
 * <li>跨类型兼容：实现{@link Value}接口，支持与{@link BigIntegerValue}、基础类型的转换与比较；</li>
 * <li>预置常量：提供{@link #ZERO}单例常量，避免重复创建零值实例。</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre class="code">
 * // 1. 构造实例（推荐字符串入参，避免浮点数精度丢失）
 * BigDecimalValue amount = new BigDecimalValue("100.34");
 * BigDecimalValue tax = new BigDecimalValue(new BigDecimal("12.56"));
 *
 * // 2. 类型转换
 * BigDecimal bigDecimal = amount.getAsBigDecimal(); // 获取原生BigDecimal
 * BigInteger bigInteger = amount.getAsBigInteger(); // 转为整数（丢弃小数部分）
 * String numStr = amount.getAsString(); // 转为字符串："100.34"
 *
 * // 3. 比较操作
 * int compare = amount.compareTo(tax); // 100.34 > 12.56 → 返回1
 * boolean isEqual = amount.equals(new BigDecimalValue("100.34")); // true
 *
 * // 4. 常用操作
 * BigDecimalValue abs = (BigDecimalValue) amount.abs(); // 取绝对值
 * BigDecimalValue negated = (BigDecimalValue) amount.negate(); // 取反：-100.34
 * </pre>
 *
 * <p>注意事项：
 * <ul>
 * <li>构造建议：优先使用字符串入参（如"100.34"），避免直接使用double（如new BigDecimalValue(0.1)可能存在精度误差）；</li>
 * <li>转换说明：{@link #getAsBigInteger()}会丢弃小数部分（如100.99 → 100），需保留小数请使用{@link #getAsBigDecimal()}；</li>
 * <li>常量使用：{@link #ZERO}为单例，不可修改，适合作为比较基准或初始值。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.math.BigDecimal 底层高精度十进制实现
 * @see BigIntegerValue 高精度整数类（支持跨类型转换与比较）
 * @see Value 数值统一接口
 */
public class BigDecimalValue extends NumberValue {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值零的常量实例（单例），避免重复创建，用于比较基准或初始值
	 */
	public static final BigDecimalValue ZERO = new BigDecimalValue(BigDecimal.ZERO);

	/** 封装的BigDecimal核心值（不可变） */
	private final BigDecimal value;

	// ------------------------------ 构造方法 ------------------------------

	/**
	 * 通过字符串构造高精度数值（推荐方式，无精度丢失）
	 *
	 * @param number 数值字符串（如"100.34"、"-56.78"），需符合BigDecimal格式
	 * @throws NumberFormatException 若字符串格式不合法（如含非数字字符、多个小数点）
	 * @throws NullPointerException 若number为null
	 */
	public BigDecimalValue(String number) {
		this(new BigDecimal(number));
	}

	/**
	 * 通过BigDecimal构造高精度数值
	 *
	 * @param value 原生BigDecimal值（不可为null）
	 * @throws NullPointerException 若value为null
	 */
	public BigDecimalValue(BigDecimal value) {
		if (value == null) {
			throw new NullPointerException("BigDecimal value must not be null");
		}
		this.value = value;
	}

	/**
	 * 通过long构造高精度数值（简化整数入参场景）
	 *
	 * @param number long类型整数（如100、-200）
	 */
	public BigDecimalValue(long number) {
		this(BigDecimal.valueOf(number));
	}

	// ------------------------------ 接口实现与核心方法 ------------------------------

	/**
	 * 获取内部封装的BigDecimal原生值（不可变，外部无法修改）
	 *
	 * @return 此实例对应的BigDecimal值（非null）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return value;
	}

	/**
	 * 将数值转为BigInteger（丢弃小数部分，仅保留整数）
	 *
	 * @return 转换后的BigInteger（如100.99 → 100）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return value.toBigInteger();
	}

	/**
	 * 获取数值的字符串表示（与原生BigDecimal格式一致）
	 *
	 * @return 数值字符串（如"100.34"、"-56.78"）
	 */
	@Override
	public String getAsString() {
		return value.toString();
	}

	/**
	 * 与另一个Value对象比较数值大小（支持跨类型）
	 *
	 * @param o 待比较的Value对象（可为null）
	 * @return 负整数=当前值＜目标值，0=相等，正整数=当前值＞目标值；若o为null，返回1（约定null小于任何数值）
	 */
	@Override
	public int compareTo(Value o) {
		if (o == null) {
			return 1;
		}
		if (o.isNumber()) {
			BigDecimal targetValue = o.getAsBigDecimal();
			return this.value.compareTo(targetValue);
		}
		return super.compareTo(o);
	}

	/**
	 * 取数值的绝对值（符号转为正）
	 *
	 * @return 新的BigDecimalValue实例（如-100.34 → 100.34）
	 */
	public NumberValue abs() {
		return new BigDecimalValue(value.abs());
	}

	/**
	 * 取数值的相反数（符号反转）
	 *
	 * @return 新的BigDecimalValue实例（如100.34 → -100.34，0 → 0）
	 * @throws ArithmeticException 理论无溢出风险，保留接口契约
	 */
	@Override
	public NumberValue negate() throws ArithmeticException {
		// 修复bug：原使用multiply(-1)，改为BigDecimal原生negate()更简洁高效
		return new BigDecimalValue(this.value.negate());
	}

	// ------------------------------ 重写Object方法 ------------------------------

	/**
	 * 重写equals：数值相等则视为相等（忽略对象引用）
	 *
	 * @param obj 待比较对象
	 * @return true=数值相等，false=不相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BigDecimalValue)) {
			return false;
		}
		BigDecimalValue other = (BigDecimalValue) obj;
		return this.value.equals(other.value);
	}

	/**
	 * 重写hashCode：基于数值计算，确保equals相等时hashCode一致
	 *
	 * @return 哈希码值
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * 重写toString：返回数值字符串，增强日志可读性
	 *
	 * @return 数值的字符串表示
	 */
	@Override
	public String toString() {
		return getAsString();
	}
}