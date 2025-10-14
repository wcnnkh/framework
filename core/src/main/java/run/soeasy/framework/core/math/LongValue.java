package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;

/**
 * 长整数实现类，继承自{@link NumberAdder}，基于Java原生{@code long}类型存储数值，
 * 提供高效的长整数运算、类型转换及状态重置能力，适用于**数值范围在long限定内**（-2⁶³~2⁶³-1，即-9223372036854775808~9223372036854775807）的场景，
 * 相比{@link IntValue}支持更大范围的整数，比{@link BigIntegerValue}更轻量、运算效率更高，是框架中处理中等规模整数（超int范围但未达long上限）的核心组件。
 * 
 * <p>设计核心：
 * <ul>
 * <li><strong>原生高效性</strong>：基于{@code long}原生类型存储，避免高精度类的内存与性能开销，
 * 适合需要处理超int范围（如>2147483647）但未达long上限的场景（如时间戳、大计数）；</li>
 * <li><strong>溢出安全控制</strong>：修改操作（如{@link #add(long)}）使用{@link Math#addExact(long, long)}，
 * 超出long范围时立即抛出{@link ArithmeticException}，杜绝隐式溢出（如9223372036854775807+1→-9223372036854775808）；</li>
 * <li><strong>状态可重置</strong>：通过{@link #initialValue}记录初始值，支持{@link #reset()}恢复状态，
 * 可复用实例减少对象创建成本（如循环处理时间戳场景）；</li>
 * <li><strong>跨类型兼容</strong>：实现{@link Value}接口，支持与{@link IntValue}、{@link BigDecimalValue}、{@link Fraction}等
 * 框架内数值类型的比较与运算，统一数值处理逻辑。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>扩展范围支持</strong>：覆盖-9e18~9e18的整数范围，远超int的2e9上限，满足时间戳（毫秒级至公元292亿年）、
 * 大计数（如千万级用户量、亿级订单数）等场景；</li>
 * <li><strong>高效运算</strong>：基于原生long运算，性能接近int，远高于{@link BigIntegerValue}，
 * 适合高频次中等规模整数计算（如日志时间戳累加、大数据量分页索引）；</li>
 * <li><strong>溢出显式处理</strong>：加法操作通过{@link Math#addExact(long, long)}确保溢出时抛异常，
 * 避免原生long的隐式溢出导致的数据错误（如时间戳计算错误）；</li>
 * <li><strong>完整类型转换</strong>：支持转为{@link BigDecimal}（无精度丢失）、{@link BigInteger}（扩展范围）、
 * {@link String}（展示用），适配不同下游场景。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>时间戳处理：存储毫秒级时间戳（如System.currentTimeMillis()返回值，范围约1970~292亿年），远超int范围；</li>
 * <li>中大规模计数：如电商平台的商品ID（可能达数亿）、用户注册量（千万级至亿级）、API调用次数（超20亿次）；</li>
 * <li>分页与索引：大数据表的分页偏移量（如offset=1000000000，超int上限）、分布式ID（通常基于long实现）；</li>
 * <li>性能敏感的中等范围运算：如金融交易中的订单编号（递增至亿级）、游戏中的角色经验值（超千万级）。</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre class="code">
 * // 1. 创建LongValue实例（初始值为long最大值）
 * LongValue timestamp = new LongValue(Long.MAX_VALUE); // 9223372036854775807
 *
 * // 2. 累加操作（支持long参数，溢出时抛异常）
 * try {
 *     timestamp.add(1); // 9223372036854775807 + 1 超出long上限
 * } catch (ArithmeticException e) {
 *     System.out.println("累加溢出：" + e.getMessage()); // 抛出"long overflow"异常
 * }
 *
 * // 3. 重置为初始值（恢复到创建时的Long.MAX_VALUE）
 * timestamp.reset();
 * System.out.println(timestamp.getAsLong()); // 输出9223372036854775807
 *
 * // 4. 跨类型比较（目标值超出long范围）
 * BigInteger bigNum = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE); // 9223372036854775808（超long上限）
 * BigIntegerValue bigIntValue = new BigIntegerValue(bigNum);
 * int compareResult = timestamp.compareTo(bigIntValue); 
 * System.out.println(compareResult); // 输出-1（timestamp < 9223372036854775808）
 *
 * // 5. 类型转换
 * BigDecimal decimal = timestamp.getAsBigDecimal(); // 9223372036854775807.0（无精度丢失）
 * BigInteger longAsBigInt = timestamp.getAsBigInteger(); // 9223372036854775807（扩展为大整数）
 * String timestampStr = timestamp.getAsString(); // "9223372036854775807"（字符串表示）
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li><strong>范围限制</strong>：仅支持long范围（-9223372036854775808~9223372036854775807），超出需使用{@link BigIntegerValue}；</li>
 * <li><strong>线程安全</strong>：无内置同步机制，多线程并发修改需外部加锁，或改用{@link java.util.concurrent.atomic.AtomicLong}
 * （若无需框架数值接口）；</li>
 * <li><strong>哈希冲突风险</strong>：当前{@link #hashCode()}实现为{@code (int) value}，会导致不同long值（如1L和4294967297L）哈希码相同，
 * 若用于哈希表（如HashMap）键，可能影响性能，建议优化为{@code Long.hashCode(value)}；</li>
 * <li><strong>溢出异常处理</strong>：{@link #add(long)}会抛出{@link ArithmeticException}，业务层需根据场景捕获（如允许溢出时转为{@link BigIntegerValue}）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see NumberAdder 父类（提供数值累加与重置的基础契约）
 * @see java.lang.Long 原生long类型的包装类，当前类基于其范围与运算规则
 * @see IntValue 普通整数实现类（数值在int范围内时推荐使用，更轻量）
 * @see BigIntegerValue 超大整数实现类（数值超出long范围时使用）
 * @see BigDecimalValue 高精度小数实现类（需与小数运算时的适配类）
 */
public class LongValue extends NumberAdder {
	private static final long serialVersionUID = 1L;

	/**
	 * 当前存储的长整数数值（基于原生{@code long}类型）
	 * <p>修改逻辑：仅通过{@link #add(long)}（累加）或{@link #reset()}（重置）更新，
	 * 且修改时会校验溢出（避免隐式溢出导致的数据错误）。
	 */
	private long value;

	/**
	 * 实例创建时的初始值，用于{@link #reset()}方法恢复状态
	 * <p>特性：初始化后不可修改，确保重置时能准确恢复到创建时的数值，作为状态复用的基准。
	 */
	private final long initialValue;

	/**
	 * 构造方法：通过指定的{@code long}初始值创建实例
	 * <p>核心逻辑：
	 * 1. 存储初始值{@code initialValue}（作为后续{@link #reset()}的基准）；
	 * 2. 调用{@link #reset()}初始化当前数值{@link #value}（与初始值一致）。
	 *
	 * @param initialValue 初始长整数数值（需在{@code long}范围内：-9223372036854775808~9223372036854775807）
	 */
	public LongValue(long initialValue) {
		this.initialValue = initialValue;
		reset();
	}

	/**
	 * 重置当前数值为创建时的初始值
	 * <p>实现逻辑：直接将{@link #value}赋值为{@link #initialValue}，无额外计算，效率极高，
	 * 适用于循环复用实例（如批量处理时间戳时的临时变量），避免重复创建{@link LongValue}对象。
	 */
	@Override
	public void reset() {
		this.value = initialValue;
	}

	/**
	 * 累加指定的{@code long}类型数值到当前值（核心修改方法）
	 * <p>执行步骤与约束：
	 * 1. 精确累加：通过{@link Math#addExact(long, long)}执行累加，若累加结果超出{@code long}范围，
	 *    立即抛出{@link ArithmeticException}（避免隐式溢出）；
	 * 2. 更新值：将累加结果赋值给{@link #value}，完成修改。
	 * <p>特殊场景：若参数为负数，等价于“当前值减去参数的绝对值”（如add(-100) → value = value - 100）。
	 *
	 * @param value 要累加的{@code long}类型数值（可正可负，累加后需在long范围内，否则抛异常）
	 * @throws ArithmeticException 若累加结果超出{@code long}范围（即长整数溢出）
	 */
	@Override
	public void add(long value) {
		this.value = Math.addExact(this.value, value);
	}

	/**
	 * 将当前长整数转为{@link BigDecimal}类型（无精度丢失）
	 * <p>转换逻辑：通过{@link BigDecimal#BigDecimal(long)}构造方法创建实例，
	 * 数值与当前{@link #value}完全一致（如value=1234567890123456789L → BigDecimal(1234567890123456789.0)），
	 * 无任何精度偏差。
	 * <p>适用场景：需与小数类型（如{@link BigDecimalValue}）进行运算，或需保留小数位的场景（如金额计算）。
	 *
	 * @return 与当前值等价的{@link BigDecimal}实例（非null，无精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	/**
	 * 将当前长整数转为{@link BigInteger}类型（扩展数值范围）
	 * <p>转换逻辑：通过{@link BigInteger#BigInteger(String)}（基于当前值的字符串）创建实例，
	 * 等价于更高效的{@link BigInteger#valueOf(long)}（因value是long，可直接适配），
	 * 转换后数值范围突破long限制，可用于后续超大整数运算。
	 * <p>优化建议：可修改为{@code return BigInteger.valueOf(value);}（语义一致，性能更优）。
	 *
	 * @return 与当前值等价的{@link BigInteger}实例（非null，范围无限制）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return new BigInteger(value + "");
	}

	/**
	 * 获取当前长整数的原生{@code long}值（直接访问，无转换开销）
	 * <p>适用场景：无需类型转换的原生long运算，如时间戳比较（value > System.currentTimeMillis()）、
	 * 大整数索引（如数组下标超出int范围时的逻辑处理）等，是所有get方法中效率最高的。
	 *
	 * @return 当前存储的原生{@code long}数值（范围：-9223372036854775808~9223372036854775807）
	 */
	@Override
	public long getAsLong() {
		return value;
	}

	/**
	 * 将当前长整数转为字符串表示（原生格式，无额外符号）
	 * <p>转换逻辑：通过{@link Long#toString(long)}实现，输出格式为十进制字符串，
	 * 例如：value=0L → "0"，value=-1234567890123456789L → "-1234567890123456789"，
	 * value=9223372036854775807L → "9223372036854775807"。
	 * <p>适用场景：日志打印、前端页面展示、接口返回值序列化等。
	 *
	 * @return 当前长整数的十进制字符串表示（非null，无多余空格或格式符）
	 */
	@Override
	public String getAsString() {
		return Long.toString(value);
	}

	/**
	 * 比较当前长整数与目标{@link Value}的数值大小（支持目标值超出long范围的场景）
	 * <p>比较规则（分场景处理）：
	 * 1. 若目标{@code o}是数值类型（{@link Value#isNumber()}返回true）：
	 *    a. 提取目标的{@link BigInteger}值（突破long范围限制）；
	 *    b. 若目标值 > {@link Long#MAX_VALUE}：当前值（long）肯定小于目标，返回-1；
	 *    c. 若目标值 < {@link Long#MIN_VALUE}：当前值（long）肯定大于目标，返回1；
	 *    d. 若目标值在long范围内：通过{@link Long#compare(long, long)}比较原生long值，返回结果；
	 * 2. 若目标非数值类型：委托父类{@link NumberAdder#compareTo(Value)}处理（默认按对象类型或引用排序）。
	 *
	 * @param o 待比较的{@link Value}对象（可为null，null视为小于任何数值类型）
	 * @return 比较结果：负整数（当前 < 目标）、0（当前 = 目标）、正整数（当前 > 目标）
	 */
	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger otherValue = o.getAsBigInteger();
			// 场景1：目标值超出long上限 → 当前值小
			if (otherValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				return -1;
			}
			// 场景2：目标值超出long下限 → 当前值大
			if (otherValue.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
				return 1;
			}
			// 场景3：目标值在long范围内 → 原生long比较
			return Long.compare(this.value, otherValue.longValue());
		}
		return super.compareTo(o);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}
}