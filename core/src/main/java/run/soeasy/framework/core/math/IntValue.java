package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;

/**
 * 原生整数实现类，继承自{@link NumberAdder}，基于Java原生{@code int}类型存储数值，
 * 提供高效的整数运算、类型转换及状态重置能力，适用于**数值范围在int限定内**（-2³¹~2³¹-1，即-2147483648~2147483647）的场景，
 * 相比{@link BigIntegerValue}更轻量、运算效率更高，是框架中处理普通整数场景的首选组件。
 * 
 * <p>设计核心：
 * <ul>
 * <li><strong>原生高效性</strong>：基于{@code int}原生类型存储，避免高精度类（如{@link BigInteger}）的额外内存开销与运算耗时，
 * 适合高频次、小范围整数计算（如计数器、索引、普通业务数值）；</li>
 * <li><strong>溢出安全控制</strong>：所有修改操作（如{@link #add(long)}）使用{@link Math#addExact(int, int)}等精确方法，
 * 一旦超出{@code int}范围立即抛出{@link ArithmeticException}，杜绝隐式溢出导致的脏数据；</li>
 * <li><strong>状态可重置</strong>：通过{@link #initialValue}记录实例创建时的初始值，支持{@link #reset()}恢复初始状态，
 * 可复用实例减少对象创建成本（如循环计数场景）；</li>
 * <li><strong>跨类型兼容</strong>：实现{@link Value}接口，支持与{@link BigDecimalValue}、{@link Fraction}等框架内数值类型的
 * 比较与运算，统一数值处理逻辑。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>高效运算</strong>：基于原生{@code int}运算，比高精度类快1~2个数量级，适合对性能敏感的场景；</li>
 * <li><strong>溢出显式处理</strong>：加法（{@link #add(long)}）使用{@link Math#toIntExact(long)}和{@link Math#addExact(int, int)}，
 * 超出{@code int}范围时主动抛异常，而非像原生{@code int}那样溢出后循环（如2147483647+1→-2147483648）；</li>
 * <li><strong>完整类型转换</strong>：支持转为{@link BigDecimal}（无精度丢失）、{@link BigInteger}（扩展范围）、
 * {@link String}（展示用），满足不同下游场景需求；</li>
 * <li><strong>范围感知比较</strong>：{@link #compareTo(Value)}可处理目标值超出{@code int}范围的场景（如目标是超大整数），
 * 自动判断大小关系，避免比较逻辑错误。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>普通计数场景：如接口调用次数、列表元素个数、订单序号等（数值通常不超过百万/千万级，远小于int上限）；</li>
 * <li>性能敏感场景：如高频交易中的订单数量统计、实时监控中的指标计数，需避免高精度类的性能开销；</li>
 * <li>有限范围整数处理：如年龄（0~150）、分数（0~100）、商品库存（通常不超过1e9，未达int上限）等明确范围的数值；</li>
 * <li>框架基础组件：作为{@link Fraction}的分子/分母（小整数）、{@link NumberValue}的基础实现，支撑更复杂的数值运算。</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre class="code">
 * // 1. 创建IntValue实例（初始值为42）
 * IntValue count = new IntValue(42);
 *
 * // 2. 累加操作（支持long参数，但会校验是否超出int范围）
 * count.add(8); // 42+8=50，正常执行
 * try {
 *     count.add(Integer.MAX_VALUE); // 50 + 2147483647 = 2147483697（超出int上限）
 * } catch (ArithmeticException e) {
 *     System.out.println("累加溢出：" + e.getMessage()); // 抛出"integer overflow"异常
 * }
 *
 * // 3. 重置为初始值（恢复到创建时的42）
 * count.reset();
 * System.out.println(count.getAsInt()); // 输出42
 *
 * // 4. 跨类型比较（目标值超出int范围）
 * BigInteger bigNum = BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE); // 2147483648（超出int上限）
 * BigIntegerValue bigIntValue = new BigIntegerValue(bigNum);
 * int compareResult = count.compareTo(bigIntValue); 
 * System.out.println(compareResult); // 输出-1（count=42 < 2147483648）
 *
 * // 5. 类型转换
 * BigDecimal decimal = count.getAsBigDecimal(); // 42.0（无精度丢失）
 * BigInteger intAsBigInt = count.getAsBigInteger(); // 42（扩展为大整数）
 * String countStr = count.getAsString(); // "42"（字符串表示）
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li><strong>范围限制</strong>：仅支持{@code int}范围（-2147483648~2147483647）的数值，超出需使用{@link BigIntegerValue}；</li>
 * <li><strong>线程安全</strong>：类中无同步机制（如synchronized、AtomicInteger），多线程并发修改需外部加锁，
 * 或改用线程安全的{@link java.util.concurrent.atomic.AtomicInteger}（若无需框架数值接口）；</li>
 * <li><strong>溢出异常</strong>：{@link #add(long)}会抛出{@link ArithmeticException}，业务层需根据场景捕获处理，
 * 避免未处理的运行时异常导致服务中断。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see NumberAdder 父类（提供数值累加与重置的基础契约）
 * @see java.lang.Integer 原生int类型的包装类，当前类基于其范围与运算规则
 * @see BigIntegerValue 超大整数实现类（当数值超出int范围时使用）
 * @see BigDecimalValue 高精度小数实现类（需与小数运算时的适配类）
 */
public class IntValue extends NumberAdder {
	private static final long serialVersionUID = 1L;

	/**
	 * 当前存储的整数数值（基于原生{@code int}类型）
	 * <p>修改逻辑：仅通过{@link #add(long)}（累加）或{@link #reset()}（重置）更新，
	 * 且修改时会校验溢出（避免隐式溢出）。
	 */
	private int value;

	/**
	 * 实例创建时的初始值，用于{@link #reset()}方法恢复状态
	 * <p>特性：初始化后不可修改，确保重置时能准确恢复到创建时的数值，避免外部篡改初始基准。
	 */
	private final int initialValue;

	/**
	 * 构造方法：通过指定的{@code int}初始值创建实例
	 * <p>核心逻辑：
	 * 1. 存储初始值{@code initialValue}（作为后续{@link #reset()}的基准）；
	 * 2. 调用{@link #reset()}初始化当前数值{@link #value}（与初始值一致）。
	 *
	 * @param initialValue 初始整数数值（需在{@code int}范围内：-2147483648~2147483647）
	 */
	public IntValue(int initialValue) {
		this.initialValue = initialValue;
		reset();
	}

	/**
	 * 重置当前数值为创建时的初始值
	 * <p>实现逻辑：直接将{@link #value}赋值为{@link #initialValue}，无额外计算，效率极高，
	 * 适用于循环复用实例（如线程池中的局部计数变量），避免重复创建{@link IntValue}对象。
	 */
	@Override
	public void reset() {
		this.value = initialValue;
	}

	/**
	 * 累加指定的{@code long}类型数值到当前值（核心修改方法）
	 * <p>执行步骤与约束：
	 * 1. 类型校验：通过{@link Math#toIntExact(long)}将{@code long}参数转为{@code int}，
	 *    若参数超出{@code int}范围（<-2147483648 或 >2147483647），立即抛出{@link ArithmeticException}；
	 * 2. 精确累加：通过{@link Math#addExact(int, int)}执行累加，若累加结果超出{@code int}范围，
	 *    同样抛出{@link ArithmeticException}；
	 * 3. 更新值：将累加结果赋值给{@link #value}，完成修改。
	 * <p>特殊场景：若参数为负数，等价于“当前值减去参数的绝对值”（如add(-5) →  value = value -5）。
	 *
	 * @param value 要累加的{@code long}类型数值（实际需在{@code int}范围内，否则抛异常）
	 * @throws ArithmeticException 若参数超出{@code int}范围，或累加结果超出{@code int}范围（即整数溢出）
	 */
	@Override
	public void add(long value) {
		// 第一步：将long参数转为int，超出范围抛异常
		int addInt = Math.toIntExact(value);
		// 第二步：精确累加，超出int范围抛异常
		this.value = Math.addExact(this.value, addInt);
	}

	/**
	 * 将当前整数转为{@link BigDecimal}类型（无精度丢失）
	 * <p>转换逻辑：通过{@link BigDecimal#BigDecimal(int)}构造方法创建实例，
	 * 数值与当前{@link #value}完全一致（如value=42 → BigDecimal(42.0)），无任何精度偏差。
	 * <p>适用场景：需与小数类型（如{@link BigDecimalValue}）进行运算，或需保留小数位的场景（如金额计算）。
	 *
	 * @return 与当前值等价的{@link BigDecimal}实例（非null，无精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	/**
	 * 将当前整数转为{@link BigInteger}类型（扩展数值范围）
	 * <p>转换逻辑：通过{@link BigInteger#BigInteger(String)}（基于当前值的字符串）创建实例，
	 * 等价于更高效的{@link BigInteger#valueOf(long)}（因value是int，可安全转为long），
	 * 转换后数值范围突破{@code int}限制，可用于后续超大整数运算。
	 * <p>优化建议：若需更高效率，可修改为{@code return BigInteger.valueOf(value);}（语义一致，性能更优）。
	 *
	 * @return 与当前值等价的{@link BigInteger}实例（非null，范围无限制）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return new BigInteger("" + value);
	}

	/**
	 * 获取当前整数的原生{@code int}值（直接访问，无转换开销）
	 * <p>适用场景：无需类型转换的原生int运算，如作为数组索引、普通计数判断（value > 0）等，
	 * 是所有get方法中效率最高的。
	 *
	 * @return 当前存储的原生{@code int}数值（范围：-2147483648~2147483647）
	 */
	@Override
	public int getAsInt() {
		return value;
	}

	/**
	 * 比较当前整数与目标{@link Value}的数值大小（支持目标值超出int范围的场景）
	 * <p>比较规则（分场景处理）：
	 * 1. 若目标{@code o}是数值类型（{@link Value#isNumber()}返回true）：
	 *    a. 提取目标的{@link BigInteger}值（突破int范围限制）；
	 *    b. 若目标值 > {@link Integer#MAX_VALUE}：当前值（int）肯定小于目标，返回-1；
	 *    c. 若目标值 < {@link Integer#MIN_VALUE}：当前值（int）肯定大于目标，返回1；
	 *    d. 若目标值在int范围内：通过{@link Integer#compare(int, int)}比较原生int值，返回结果；
	 * 2. 若目标非数值类型：委托父类{@link NumberAdder#compareTo(Value)}处理（默认按对象类型或引用排序）。
	 *
	 * @param o 待比较的{@link Value}对象（可为null，null视为小于任何数值类型）
	 * @return 比较结果：负整数（当前 < 目标）、0（当前 = 目标）、正整数（当前 > 目标）
	 */
	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger otherValue = o.getAsBigInteger();
			// 场景1：目标值超出int上限 → 当前值小
			if (otherValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				return -1;
			}
			// 场景2：目标值超出int下限 → 当前值大
			if (otherValue.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
				return 1;
			}
			// 场景3：目标值在int范围内 → 原生int比较
			return Integer.compare(this.value, otherValue.intValue());
		}
		return super.compareTo(o);
	}

	/**
	 * 将当前整数转为字符串表示（原生格式，无额外符号）
	 * <p>转换逻辑：通过{@link Integer#toString(int)}实现，输出格式为十进制字符串，
	 * 例如：value=0 → "0"，value=-123 → "-123"，value=2147483647 → "2147483647"。
	 * <p>适用场景：日志打印、前端页面展示、接口返回值序列化等。
	 *
	 * @return 当前整数的十进制字符串表示（非null，无多余空格或格式符）
	 */
	@Override
	public String getAsString() {
		return Integer.toString(value);
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}
}