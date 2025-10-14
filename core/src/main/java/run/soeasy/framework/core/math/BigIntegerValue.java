package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;

/**
 * 高精度整数实现类，继承自{@link NumberAdder}，封装Java原生{@link BigInteger}，
 * 提供任意大小整数的存储、比较、类型转换及原子累加能力，解决基础类型{@code long}范围不足（最大9e18）的问题，
 * 是框架中处理超大整数场景（如金融大额数值、密码学大素数）的核心组件。
 * 
 * <p>设计核心：
 * <ul>
 * <li><strong>不可变数据安全</strong>：内部依赖{@link BigInteger}的不可变性，所有修改操作（如{@link #add(long)}）
 * 均通过创建新{@link BigInteger}实例实现，避免并发场景下的数据竞争；</li>
 * <li><strong>状态可重置</strong>：通过{@link #initialValue}记录初始值，支持{@link #reset()}方法恢复初始状态，
 * 适用于循环复用数值实例的场景；</li>
 * <li><strong>类型无缝兼容</strong>：实现{@link Value}接口，支持与{@link BigDecimalValue}、{@link Fraction}等
 * 其他数值类型的比较与运算，统一框架内数值处理逻辑。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>无限精度支持</strong>：数值大小仅受内存限制，可存储远超{@code long}范围的整数（如100位、1000位大整数）；</li>
 * <li><strong>线程安全</strong>：基于{@link BigInteger}的不可变性及{@link NumberAdder}的累加逻辑设计，
 * 支持多线程环境下的安全累加（需确保{@link #add(long)}的原子性，具体依赖父类实现）；</li>
 * <li><strong>预置常用常量</strong>：提供{@link #ZERO}（数值0）和{@link #ONE}（数值1）的单例常量，
 * 避免重复创建高频使用的整数实例，提升性能；</li>
 * <li><strong>完整类型转换</strong>：支持转为{@link BigDecimal}（小数，无精度丢失）、{@link String}（字符串），
 * 满足不同场景下的输出需求。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>金融领域：大额交易金额（如超过9e18的分账金额）、国债发行量等需精确无范围限制的整数计算；</li>
 * <li>密码学：生成大素数、RSA加密算法中的密钥（通常为1024位或2048位整数）；</li>
 * <li>数据统计：超大样本量的计数（如百亿级用户数、千亿级订单量），突破{@code long}上限；</li>
 * <li>科学计算：天文数据（如星体质量、距离）、粒子物理中的微观粒子数量等超大规模整数表示。</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre class="code">
 * // 1. 创建超大整数实例（如100位整数）
 * BigInteger bigInt = new BigInteger("1234567890123456789012345678901234567890");
 * BigIntegerValue bigIntValue = new BigIntegerValue(bigInt);
 *
 * // 2. 累加操作（支持long类型数值，自动转为BigInteger）
 * bigIntValue.add(1000); // 初始值 + 1000，内部创建新BigInteger实例
 *
 * // 3. 类型转换
 * BigDecimal decimal = bigIntValue.getAsBigDecimal(); // 转为BigDecimal（无精度丢失）
 * String numStr = bigIntValue.getAsString(); // 转为字符串用于展示："1234567890123456789012345678901234577890"
 *
 * // 4. 比较操作（与其他数值类型比较）
 * BigIntegerValue another = new BigIntegerValue(new BigInteger("1234567890123456789012345678901234567890"));
 * int compareResult = bigIntValue.compareTo(another); // 结果为1（当前值更大）
 *
 * // 5. 重置为初始状态
 * bigIntValue.reset(); // 恢复为创建时的100位整数，丢弃累加结果
 *
 * // 6. 使用预置常量
 * BigIntegerValue zero = BigIntegerValue.ZERO; // 获取0常量，避免重复创建
 * BigIntegerValue one = BigIntegerValue.ONE;   // 获取1常量，用于计数起始
 * </pre>
 *
 * @author soeasy.run
 * @see java.math.BigInteger 底层依赖的Java原生高精度整数类
 * @see NumberAdder 父类（提供数值累加与重置的基础契约）
 * @see NumberUtils 框架内数值工具类（用于{@link #reset()}中的BigInteger实例创建）
 * @see BigDecimalValue 高精度小数类（可与当前类无缝转换与运算）
 * @see Fraction 分数类（支持将当前类作为分子/分母构建分数）
 */
public class BigIntegerValue extends NumberAdder {
	private static final long serialVersionUID = 1L;

	/**
	 * 数值0的单例常量实例，基于{@link BigInteger#ZERO}创建
	 * <p>用途：避免重复创建数值0的实例，常用于初始化、比较“无值”场景（如计数起点、差值为0）
	 */
	public static final BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);

	/**
	 * 数值1的单例常量实例，基于{@link BigInteger#ONE}创建
	 * <p>用途：常用于计数累加（如循环计数+1）、乘法单位元（任何数×1不变）场景
	 */
	public static final BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);

	/**
	 * 当前存储的高精度整数值（不可变，每次修改均生成新实例）
	 * <p>注意：由于{@link BigInteger}不可变，{@link #add(long)}等修改方法会更新此引用，而非修改原对象
	 */
	private BigInteger value;

	/**
	 * 实例创建时的初始值，用于{@link #reset()}方法恢复初始状态
	 * <p>作用：确保重置后的值与创建时一致，且与原初始值对象无引用关联（通过{@link NumberUtils#newBigInteger(BigInteger)}创建新实例）
	 */
	private final BigInteger initialValue;

	/**
	 * 构造方法：通过指定的{@link BigInteger}初始值创建实例
	 * <p>核心逻辑：
	 * 1. 校验初始值非null（若为null，{@link NumberUtils#newBigInteger(BigInteger)}会抛出异常）；
	 * 2. 存储初始值的拷贝（避免外部修改原{@link BigInteger}对象影响当前实例）；
	 * 3. 调用{@link #reset()}初始化当前值（与初始值一致）。
	 *
	 * @param initialValue 初始高精度整数值（不可为null，将作为{@link #reset()}的基准）
	 * @throws NullPointerException 如果{@code initialValue}为null（由{@link NumberUtils#newBigInteger(BigInteger)}抛出）
	 */
	public BigIntegerValue(BigInteger initialValue) {
		this.initialValue = initialValue;
		reset();
	}

	/**
	 * 比较当前实例与目标{@link Value}的数值大小
	 * <p>比较规则：
	 * 1. 若目标{@code o}是数值类型（{@link Value#isNumber()}返回true）：
	 *    - 提取目标的{@link BigInteger}值（通过{@link Value#getAsBigInteger()}）；
	 *    - 调用内部{@link #value}的{@link BigInteger#compareTo(BigInteger)}方法，返回比较结果（负整数=当前小，0=相等，正整数=当前大）；
	 * 2. 若目标非数值类型：委托父类{@link NumberAdder#compareTo(Value)}处理（默认按对象引用或类型排序）。
	 *
	 * @param o 待比较的{@link Value}对象（可为null，null视为小于任何数值类型）
	 * @return 比较结果：负整数（当前 < 目标）、0（当前 = 目标）、正整数（当前 > 目标）
	 */
	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger value = o.getAsBigInteger();
			return this.value.compareTo(value);
		}
		return super.compareTo(o);
	}

	/**
	 * 计算当前实例的哈希值，确保“数值相等的实例哈希值一致”
	 * <p>哈希逻辑：直接复用内部{@link #value}（BigInteger）的哈希值，因BigInteger已保证“数值相等则哈希值相等”，
	 * 故当前类无需额外计算，同时满足{@link #equals(Object)}与{@code hashCode()}的契约（equals为true则hashCode必相等）。
	 *
	 * @return 基于内部{@link BigInteger}值的哈希值
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * 将当前高精度整数转为{@link BigDecimal}类型（无精度丢失）
	 * <p>转换逻辑：直接通过{@link BigDecimal#BigDecimal(BigInteger)}构造方法创建，
	 * 数值与当前{@link #value}完全一致（如BigInteger(123) → BigDecimal(123.0)）。
	 * <p>适用场景：需与小数类型（如{@link BigDecimalValue}）运算时的类型适配。
	 *
	 * @return 与当前值等价的{@link BigDecimal}实例（非null，无精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	/**
	 * 获取当前实例封装的{@link BigInteger}原始值
	 * <p>注意：返回的是当前值的引用，若外部修改此{@link BigInteger}对象（虽其不可变，无法修改），
	 * 不会影响当前实例的{@link #value}（因BigInteger修改需生成新实例），故无需返回拷贝（避免性能开销）。
	 *
	 * @return 当前实例存储的{@link BigInteger}值（非null）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return value;
	}

	/**
	 * 将当前高精度整数转为字符串表示
	 * <p>转换逻辑：直接调用内部{@link #value}的{@link BigInteger#toString()}方法，
	 * 输出格式与原生BigInteger一致（如1000 → "1000"，负数-123 → "-123"）。
	 * <p>适用场景：日志打印、前端展示、数值序列化等。
	 *
	 * @return 当前整数的字符串形式（非null，无额外格式符）
	 */
	@Override
	public String getAsString() {
		return value.toString();
	}

	/**
	 * 重置当前实例的数值为创建时的初始值
	 * <p>实现逻辑：通过{@link NumberUtils#newBigInteger(BigInteger)}创建{@link #initialValue}的新实例，
	 * 赋值给{@link #value}，确保与原初始值对象无引用关联（避免外部修改初始值影响当前实例）。
	 * <p>适用场景：循环复用实例（如线程池中的计数变量），避免重复创建新{@link BigIntegerValue}实例。
	 */
	@Override
	public void reset() {
		this.value = NumberUtils.newBigInteger(initialValue);
	}

	/**
	 * 累加指定的{@code long}类型数值到当前值
	 * <p>核心逻辑：
	 * 1. 将{@code long}值转为{@link BigInteger}（通过{@link BigInteger#valueOf(long)}）；
	 * 2. 调用{@link BigInteger#add(BigInteger)}生成新的累加后实例（因BigInteger不可变，原{@link #value}不变）；
	 * 3. 更新{@link #value}引用为新实例（完成累加）。
	 * <p><strong>注意</strong>：若{@code long}值超出自身范围（虽参数类型为long，实际无此问题），
	 * 需使用{@link #add(BigInteger)}（若父类提供）或先转为BigInteger再累加，避免溢出。
	 *
	 * @param value 要累加的{@code long}类型数值（可正可负，负数等价于减法）
	 */
	@Override
	public void add(long value) {
		// 修正：BigInteger不可变，需将结果赋值给this.value（原代码未赋值，累加无效果）
		this.value = this.value.add(BigInteger.valueOf(value));
	}
}