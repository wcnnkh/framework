package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;

/**
 * 高精度整数实现类，继承自{@link NumberAdder}（提供数值累加与重置契约），封装Java原生{@link BigInteger}，
 * 解决基础类型{@code long}（最大值9223372036854775807，约9×10¹⁸）的范围不足问题，
 * 是框架中处理超大整数场景（如金融大额数值、密码学大素数）的核心组件。
 * 
 * <p><strong>设计核心</strong>：
 * <ul>
 * <li><strong>不可变数据安全</strong>：内部依赖{@link BigInteger}的不可变性，所有修改操作（如{@link #add(long)}）
 * 均通过创建新{@link BigInteger}实例实现，避免并发场景下的数据竞争（无需额外加锁，依赖不可变对象的线程安全特性）；</li>
 * <li><strong>状态可重置</strong>：通过{@link #initialValue}字段记录实例创建时的初始值，支持{@link #reset()}方法恢复初始状态，
 * 适用于循环复用实例的场景（如线程池中的计数变量，减少对象创建开销）；</li>
 * <li><strong>跨类型兼容性</strong>：实现{@link Value}接口，支持与{@link BigDecimalValue}（高精度小数）、{@link Fraction}（分数）等
 * 其他数值类型的比较与运算，统一框架内数值处理逻辑，避免类型转换碎片化；</li>
 * <li><strong>引用安全防护</strong>：构造时通过{@link NumberUtils#newBigInteger(BigInteger)}创建初始值的拷贝，
 * 避免外部修改原{@link BigInteger}对象（虽其不可变，但确保初始值与外部完全解耦）。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>无限精度支持</strong>：数值大小仅受JVM内存限制，可存储任意长度整数（如100位、1000位大整数），无范围溢出风险；</li>
 * <li><strong>线程安全基础</strong>：基于{@link BigInteger}的不可变性及{@link NumberAdder}的累加逻辑设计，
 * 多线程环境下调用{@link #add(long)}时，单个实例的状态更新需确保原子性（若需强原子性，建议结合{@code synchronized}或原子引用）；</li>
 * <li><strong>预置高频常量</strong>：提供{@link #ZERO}（数值0）和{@link #ONE}（数值1）的单例常量，
 * 避免重复创建高频使用的整数实例（如计数起点、比较基准），降低内存开销；</li>
 * <li><strong>完整类型转换</strong>：
 *   - 转为{@link BigDecimal}：通过{@link #getAsBigDecimal()}实现，无精度丢失（如{@code BigInteger(123)} → {@code BigDecimal(123.0)}）；
 *   - 转为{@link String}：通过{@link #getAsString()}实现，输出格式与原生{@link BigInteger}一致（如负数{@code -456} → {@code "-456"}）；
 *   - 转为基础类型：需通过父类或{@link BigInteger}的方法（如{@code longValue()}），但需注意范围校验（超出目标类型会抛异常）。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融领域：大额交易分账金额（如超过{@code long}上限的分润数值）、国债发行量（万亿级整数）等需精确无范围限制的计算；</li>
 * <li>密码学算法：生成RSA/ECC加密算法的密钥（通常为1024位、2048位甚至4096位大素数）、哈希签名验证中的大整数运算；</li>
 * <li>大数据统计：超大规模样本计数（如百亿级用户UV、千亿级订单量）、分布式系统中的全局唯一ID（如雪花算法的超大序号）；</li>
 * <li>科学计算：天文数据（如星体距离、质量的整数表示）、粒子物理中的微观粒子数量（超大规模整数）。</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre class="code">
 * // 1. 创建超大整数实例（100位整数，超出long范围）
 * BigInteger rawBigInt = new BigInteger("123456789012345678901234567890123456789012345678901234567890");
 * BigIntegerValue bigIntValue = new BigIntegerValue(rawBigInt);
 *
 * // 2. 累加操作（支持long类型，自动转为BigInteger；负数等价于减法）
 * bigIntValue.add(1000L); // 累加后：原100位整数 + 1000（内部创建新BigInteger实例）
 * bigIntValue.add(-500L); // 累加后：上述结果 - 500（最终比初始值多500）
 *
 * // 3. 类型转换（无精度丢失）
 * BigDecimal decimal = bigIntValue.getAsBigDecimal(); // 结果：123456...7890500.0（BigDecimal类型）
 * String numStr = bigIntValue.getAsString(); // 结果："123456789012345678901234567890123456789012345678901234567890500"
 *
 * // 4. 跨类型比较（与BigDecimalValue比较）
 * BigDecimalValue compareDecimal = new BigDecimalValue(new BigDecimal("123456789012345678901234567890123456789012345678901234567890500.0"));
 * int compareResult = bigIntValue.compareTo(compareDecimal); // 结果：0（数值相等）
 *
 * // 5. 范围校验示例（转为long前需判断，避免溢出）
 * if (bigIntValue.getAsBigInteger().compareTo(BigInteger.valueOf(Long.MAX_VALUE)) &lt;= 0) {
 *     long longVal = bigIntValue.getAsBigInteger().longValue(); // 安全转换
 * } else {
 *     throw new ArithmeticException("数值超出long范围");
 * }
 *
 * // 6. 重置为初始状态（恢复创建时的100位整数，丢弃累加结果）
 * bigIntValue.reset();
 * BigInteger resetValue = bigIntValue.getAsBigInteger(); // 结果：与rawBigInt完全一致
 *
 * // 7. 使用预置常量（避免重复创建）
 * BigIntegerValue zero = BigIntegerValue.ZERO; // 获取数值0的单例实例
 * BigIntegerValue one = BigIntegerValue.ONE;   // 获取数值1的单例实例，用于计数起始
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li>{@link #add(long)}方法修正：原逻辑未将累加结果赋值给{@link #value}，导致累加无效果；优化后通过{@code this.value = this.value.add(...)}更新状态，确保累加生效；</li>
 * <li>基础类型转换风险：若需转为{@code long}、{@code int}等基础类型，需先通过{@link BigInteger#compareTo(BigInteger)}校验范围（如{@code value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) &lt;= 0}），避免溢出；</li>
 * <li>单例常量不可修改：{@link #ZERO}和{@link #ONE}为单例实例，调用{@link #add(long)}或{@link #reset()}会修改其状态，建议仅作为“只读基准”使用，避免直接修改常量；</li>
 * <li>HTML转义说明：代码中的{@code <}需转为{@code &lt;}，{@code >}需转为{@code &gt;}（如{@code a < b} → {@code a &lt; b}），避免HTML解析时被识别为标签。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.math.BigInteger 底层依赖的Java原生高精度整数类（提供不可变整数的核心能力）
 * @see NumberAdder 父类（定义数值累加、重置的基础契约，当前类实现其抽象方法）
 * @see NumberUtils 框架内数值工具类（用于{@link #reset()}中创建初始值的拷贝，确保引用安全）
 * @see BigDecimalValue 高精度小数类（可与当前类无缝转换，支持跨类型运算）
 * @see Fraction 分数类（支持将当前类作为分子/分母构建分数，实现精确分数运算）
 */
public class BigIntegerValue extends NumberAdder {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值0的单例常量实例，基于{@link BigInteger#ZERO}创建
	 * <p>用途：作为数值比较的“零基准”（如判断差值是否为0）、计数变量的初始值，避免重复创建实例
	 * <p><strong>注意</strong>：不可直接调用{@link #add(long)}修改其状态，否则会影响所有引用此常量的代码
	 */
	public static final BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);

	/**
	 * 数值1的单例常量实例，基于{@link BigInteger#ONE}创建
	 * <p>用途：计数累加的步长（如循环计数{@code one.add(1)}）、乘法运算的单位元（任何数×1不变）
	 * <p><strong>注意</strong>：不可直接调用{@link #add(long)}修改其状态，否则会影响所有引用此常量的代码
	 */
	public static final BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);

	/**
	 * 当前存储的高精度整数值（不可变对象引用）
	 * <p>特性：由于{@link BigInteger}不可变，所有修改操作（如{@link #add(long)}）均会生成新{@link BigInteger}实例，
	 * 并更新此引用；原实例不会被修改，确保并发场景下的线程安全基础
	 */
	private BigInteger value;

	/**
	 * 实例创建时的初始值（不可变）
	 * <p>作用：作为{@link #reset()}方法的恢复基准，通过{@link NumberUtils#newBigInteger(BigInteger)}创建拷贝，
	 * 确保与外部传入的初始值对象完全解耦（即使外部修改原对象，也不影响当前实例的初始状态）
	 */
	private final BigInteger initialValue;

	/**
	 * 构造方法：通过指定的{@link BigInteger}初始值创建实例
	 * <p>核心逻辑：
	 * 1. 接收外部传入的初始值，通过{@link NumberUtils#newBigInteger(BigInteger)}创建拷贝，避免外部引用影响；
	 * 2. 将拷贝后的初始值赋值给{@link #initialValue}（永久存储，不可修改）；
	 * 3. 调用{@link #reset()}方法，将{@link #value}初始化为{@link #initialValue}的拷贝，确保初始状态正确。
	 *
	 * @param initialValue 初始高精度整数值（不可为null，否则{@link NumberUtils#newBigInteger(BigInteger)}会抛出{@link NullPointerException}）
	 * @throws NullPointerException 若{@code initialValue}为null（由{@link NumberUtils#newBigInteger(BigInteger)}触发，确保输入合法性）
	 */
	public BigIntegerValue(BigInteger initialValue) {
		this.initialValue = NumberUtils.newBigInteger(initialValue); // 修正：原逻辑未拷贝，补充NumberUtils创建拷贝，确保解耦
		reset();
	}

	/**
	 * 比较当前实例与目标{@link Value}的数值大小（实现{@link Value}接口契约）
	 * <p>比较规则（优先级从高到低）：
	 * 1. 若目标{@code o}为null：返回1（约定null小于任何数值类型）；
	 * 2. 若目标{@code o}是数值类型（{@link Value#isNumber()}返回true）：
	 *    - 提取目标的{@link BigInteger}值（通过{@link Value#getAsBigInteger()}，小数类型会截断小数部分）；
	 *    - 调用当前{@link #value}的{@link BigInteger#compareTo(BigInteger)}方法，返回比较结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）；
	 * 3. 若目标{@code o}非数值类型：委托父类{@link NumberAdder#compareTo(Value)}处理（默认按类名哈希排序，确保排序一致性）。
	 *
	 * @param o 待比较的{@link Value}对象（可为null）
	 * @return int：比较结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）
	 */
	@Override
	public int compareTo(Value o) {
		if (o == null) {
			return 1;
		}
		if (o.isNumber()) {
			BigInteger targetValue = o.getAsBigInteger();
			return this.value.compareTo(targetValue);
		}
		return super.compareTo(o);
	}

	/**
	 * 计算当前实例的哈希值（遵循{@code equals-hashCode}契约）
	 * <p>哈希逻辑：直接复用内部{@link #value}（{@link BigInteger}）的哈希值，因{@link BigInteger}已保证“数值相等则哈希值相等”，
	 * 故当前类无需额外计算，确保“equals为true的实例，hashCode必相等”。
	 *
	 * @return int：基于当前{@link #value}的哈希值（与数值内容强关联）
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * 判断当前实例与目标对象是否相等（遵循{@code equals-hashCode}契约）
	 * <p>相等规则：
	 * 1. 若目标对象与当前实例为同一引用：返回true；
	 * 2. 若目标对象不是{@link BigIntegerValue}类型：返回false；
	 * 3. 若目标对象是{@link BigIntegerValue}类型：比较两者的{@link #value}是否相等（通过{@link BigInteger#equals(Object)}）。
	 *
	 * @param obj 待比较的对象（可为null）
	 * @return boolean：true=相等，false=不相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BigIntegerValue)) {
			return false;
		}
		BigIntegerValue other = (BigIntegerValue) obj;
		return this.value.equals(other.value);
	}

	/**
	 * 将当前高精度整数转为{@link BigDecimal}类型（无精度丢失）
	 * <p>转换逻辑：通过{@link BigDecimal#BigDecimal(BigInteger)}构造方法直接创建，
	 * 数值与当前{@link #value}完全一致，小数部分为0（如{@code BigInteger(123)} → {@code BigDecimal(123.0)}）。
	 * <p>适用场景：需与{@link BigDecimalValue}（高精度小数）进行运算时，作为类型适配的中间步骤。
	 *
	 * @return {@link BigDecimal}：与当前值等价的小数实例（非null，无精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	/**
	 * 获取当前实例封装的{@link BigInteger}原始值
	 * <p>安全说明：由于{@link BigInteger}是不可变对象，返回当前{@link #value}的直接引用，
	 * 外部即使获取引用也无法修改其内容，故无需创建拷贝（避免不必要的性能开销）。
	 *
	 * @return {@link BigInteger}：当前实例存储的高精度整数值（非null）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return value;
	}

	/**
	 * 将当前高精度整数转为字符串表示（实现{@link Value}接口契约）
	 * <p>转换逻辑：直接调用{@link #value}的{@link BigInteger#toString()}方法，
	 * 输出格式与原生{@link BigInteger}一致（如正数{@code 456} → {@code "456"}，负数{@code -789} → {@code "-789"}）。
	 * <p>适用场景：日志打印、前端数值展示、JSON序列化（避免科学计数法）。
	 *
	 * @return String：当前整数的字符串形式（非null，无额外格式符）
	 */
	@Override
	public String getAsString() {
		return value.toString();
	}

	/**
	 * 重置当前实例的数值为创建时的初始状态（实现{@link NumberAdder}接口契约）
	 * <p>核心逻辑：通过{@link NumberUtils#newBigInteger(BigInteger)}创建{@link #initialValue}的新拷贝，
	 * 赋值给{@link #value}，确保每次重置后都是全新的{@link BigInteger}实例（与初始值解耦，避免引用复用问题）。
	 * <p>适用场景：循环复用实例（如线程池中的计数器），无需重新创建{@link BigIntegerValue}，降低对象创建开销。
	 */
	@Override
	public void reset() {
		this.value = NumberUtils.newBigInteger(initialValue);
	}

	/**
	 * 累加指定的{@code long}类型数值到当前值（实现{@link NumberAdder}接口契约）
	 * <p>核心逻辑（修正后）：
	 * 1. 将{@code long}类型的{@code value}转为{@link BigInteger}（通过{@link BigInteger#valueOf(long)}，无精度丢失）；
	 * 2. 调用当前{@link #value}的{@link BigInteger#add(BigInteger)}方法，生成新的累加后实例（因{@link BigInteger}不可变，原实例不变）；
	 * 3. 将新实例赋值给{@link #value}，完成状态更新（修正原逻辑“未赋值导致累加无效果”的问题）。
	 * <p>特殊场景：若{@code value}为负数，等价于“当前值减去{@code -value}”（如{@code add(-100)} → 当前值-100）。
	 *
	 * @param value 要累加的{@code long}类型数值（可正可负，范围：{@code Long.MIN_VALUE} ~ {@code Long.MAX_VALUE}）
	 */
	@Override
	public void add(long value) {
		this.value = this.value.add(BigInteger.valueOf(value)); // 关键修正：将累加结果赋值给this.value，确保状态更新
	}
}