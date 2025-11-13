package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 原生整数实现类，继承自{@link NumberAdder}，基于Java原生{@code int}类型存储数值，
 * 聚焦**int范围（-2³¹~2³¹-1，即-2147483648~2147483647）** 内的高效运算，
 * 相比{@link BigIntegerValue}省去高精度对象的内存与运算开销，是框架中处理普通整数场景（如计数、索引、有限范围数值）的首选轻量组件。
 * 
 * <p>
 * <strong>设计核心（三大核心目标）</strong>：
 * <ul>
 * <li><strong>极致轻量高效</strong>：依托原生{@code int}的栈内存特性（相对对象的堆内存），减少内存占用；
 * 运算基于JVM原生指令（如{@code iadd}），比高精度类（{@link BigInteger}）快1~2个数量级，适配高频次计算场景；</li>
 * <li><strong>溢出安全可控</strong>：所有数值修改（如{@link #add(long)}）均通过{@link Math}精确方法（{@link Math#addExact(int, int)}）实现，
 * 一旦超出int范围立即抛出{@link ArithmeticException}，杜绝原生int“溢出循环”问题（如{@code 2147483647+1=-2147483648}）；</li>
 * <li><strong>场景化复用</strong>：通过{@link #initialValue}存储创建时的初始值，支持{@link #reset()}一键恢复初始状态，
 * 可循环复用实例（如线程池局部计数器），减少对象创建与GC开销。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>原生运算性能</strong>：加法、类型转换（如{@link #getAsInt()}）均无额外对象创建，直接操作原生值，适合对性能敏感的场景（如实时监控计数）；</li>
 * <li><strong>严格溢出防护</strong>： -
 * {@link #add(long)}先通过{@link Math#toIntExact(long)}校验参数是否超int范围； -
 * 再通过{@link Math#addExact(int, int)}校验累加结果是否超int范围； 双重防护确保数值始终在int内；</li>
 * <li><strong>无缝类型兼容</strong>： -
 * 转{@link BigDecimal}/{@link BigInteger}：无精度丢失，支持与高精度类型运算； -
 * 转{@link String}：原生十进制格式（如{@code -123}→"{@code -123}"），无多余格式符；
 * 适配框架内所有数值类型的交互；</li>
 * <li><strong>范围感知比较</strong>：{@link #compareTo(Value)}可处理目标值超int范围的场景（如目标是{@link BigIntegerValue}），
 * 自动判断“当前int值”与“超范围值”的大小（如int最大值&lt;超int值），避免比较逻辑错误。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>普通计数场景：接口调用次数、列表元素索引、订单序号（通常≤千万级，远低于int上限）；</li>
 * <li>有限范围数值：年龄（0~150）、考试分数（0~100）、商品库存（通常≤1e9，未达int上限）；</li>
 * <li>性能敏感场景：高频交易中的订单数量统计、实时日志的行数计数，需避免高精度类的性能损耗；</li>
 * <li>基础组件依赖：作为{@link Fraction}的分子/分母（小整数）、{@link NumberValue}的基础实现，支撑复杂数值运算的“轻量单元”。</li>
 * </ul>
 *
 * <h3>使用示例（含关键场景）</h3>
 * 
 * <pre class="code">
 * // 1. 创建实例（初始值为42，在int范围内）
 * IntValue counter = new IntValue(42);
 *
 * // 2. 正常累加（参数在int范围内，无异常）
 * counter.add(8); // 42+8=50，value更新为50
 * System.out.println(counter.getAsInt()); // 输出：50
 *
 * // 3. 溢出场景（参数/结果超int范围，抛异常）
 * try {
 * 	// 场景1：参数超int上限（2147483648 &gt; Integer.MAX_VALUE）
 * 	counter.add((long) Integer.MAX_VALUE + 1);
 * } catch (ArithmeticException e) {
 * 	System.out.println("参数溢出：" + e.getMessage()); // 输出："integer overflow"
 * }
 *
 * try {
 * 	// 场景2：累加结果超int上限（50 + 2147483647 = 2147483697 &gt; Integer.MAX_VALUE）
 * 	counter.add(Integer.MAX_VALUE);
 * } catch (ArithmeticException e) {
 * 	System.out.println("结果溢出：" + e.getMessage()); // 输出："integer overflow"
 * }
 *
 * // 4. 重置为初始值（恢复到创建时的42，无计算开销）
 * counter.reset();
 * System.out.println(counter.getAsInt()); // 输出：42
 *
 * // 5. 跨类型比较（目标值超int范围）
 * BigInteger bigNum = BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE); // 2147483648（超int上限）
 * BigIntegerValue bigIntValue = new BigIntegerValue(bigNum);
 * int compareRes1 = counter.compareTo(bigIntValue); // 42 &lt; 2147483648 → 返回-1
 *
 * IntValue another = new IntValue(42);
 * int compareRes2 = counter.compareTo(another); // 42 == 42 → 返回0
 *
 * // 6. 类型转换（无精度丢失/范围扩展）
 * BigDecimal bd = counter.getAsBigDecimal(); // 42.0（无精度丢失，支持小数运算）
 * BigInteger bi = counter.getAsBigInteger(); // 42（扩展为大整数，支持超int运算）
 * String str = counter.getAsString(); // "42"（用于展示/序列化）
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li><strong>范围严格限制</strong>：仅支持int范围数值，超出需替换为{@link BigIntegerValue}，避免强行使用导致溢出异常；</li>
 * <li><strong>线程安全警告</strong>：类内无同步机制（如{@code synchronized}、{@link java.util.concurrent.atomic.AtomicInteger}），
 * 多线程并发修改需外部加锁（如{@code synchronized (counter) { counter.add(1);
 * }}），或改用线程安全的原子类（若无需框架{@link Value}接口）；</li>
 * <li><strong>异常必须处理</strong>：{@link #add(long)}会主动抛出{@link ArithmeticException}，业务层需根据场景捕获（如“库存超上限时提示用户”），
 * 避免未处理异常导致服务中断；</li>
 * <li><strong>{@link #getAsBigInteger()}优化建议</strong>：当前实现为{@code new BigInteger("" + value)}，可优化为{@code BigInteger.valueOf(value)}，
 * 两者语义一致，但后者避免字符串拼接，性能更优（推荐项目编译前替换）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see NumberAdder 父类（定义数值累加、重置的基础契约，当前类实现其核心方法）
 * @see java.lang.Integer 原生int的包装类，当前类基于其范围（-2147483648~2147483647）与运算规则
 * @see BigIntegerValue 超大整数实现类（数值超int范围时的替代方案）
 * @see BigDecimalValue 高精度小数实现类（需与小数交互时的适配类）
 * @see Math#addExact(int, int) 用于精确累加的原生方法（支撑溢出防护）
 */
public class IntValue extends NumberAdder {
	private static final long serialVersionUID = 1L;

	/**
	 * 当前存储的原生int数值（核心状态字段）
	 * <p>
	 * 修改约束：仅允许通过{@link #add(long)}（累加）或{@link #reset()}（重置）更新，
	 * 禁止直接赋值，确保所有修改均经过溢出校验；
	 * <p>
	 * 取值范围：始终在int范围内（-2147483648~2147483647），由{@link #add(long)}的溢出防护保障。
	 */
	private int value;

	/**
	 * 实例创建时的初始值（不可变基准）
	 * <p>
	 * 作用：作为{@link #reset()}的恢复目标，初始化后不可修改，确保每次重置都能回到创建时的初始状态；
	 * <p>
	 * 特性：存储时直接保留构造参数值，无额外转换，保证初始值的准确性。
	 */
	private final int initialValue;

	/**
	 * 构造方法：通过原生int初始值创建实例
	 * <p>
	 * 核心逻辑： 1. 存储初始值到{@link #initialValue}（不可变，作为后续重置的基准）； 2.
	 * 调用{@link #reset()}将{@link #value}初始化为初始值，确保实例创建即处于可用状态。
	 *
	 * @param initialValue 初始数值（必须在int范围内：-2147483648~2147483647，超出会导致构造时即触发溢出，但JVM编译时会拦截超范围字面量）
	 */
	public IntValue(int initialValue) {
		this.initialValue = initialValue;
		reset();
	}

	/**
	 * 重置当前数值为创建时的初始值
	 * <p>
	 * 实现逻辑：直接将{@link #value}赋值为{@link #initialValue}，无任何计算或对象创建， 时间复杂度O(1)，效率极高；
	 * <p>
	 * 适用场景：循环复用实例（如线程池中的局部计数变量），避免重复创建{@link IntValue}导致的GC压力。
	 */
	@Override
	public void reset() {
		this.value = initialValue;
	}

	/**
	 * 累加指定long数值到当前值（核心修改方法，含双重溢出防护）
	 * <p>
	 * 执行步骤（严格顺序）： 1.
	 * <strong>参数校验</strong>：通过{@link Math#toIntExact(long)}将long参数转为int，
	 * 若参数&lt;-2147483648或&gt;2147483647，立即抛出{@link ArithmeticException}（参数溢出）； 2.
	 * <strong>精确累加</strong>：通过{@link Math#addExact(int, int)}计算“当前value +
	 * 转换后的int参数”， 若结果超int范围，抛出{@link ArithmeticException}（结果溢出）； 3.
	 * <strong>状态更新</strong>：将累加结果赋值给{@link #value}，完成状态修改。
	 * <p>
	 * 特殊处理：若参数为负数（如-5），等价于“当前值减去参数绝对值”（即value = value - 5），无需额外判断。
	 *
	 * @param value 待累加的long数值（实际需在int范围内，否则第一步即抛异常）
	 * @throws ArithmeticException 若参数超int范围（参数溢出），或累加结果超int范围（结果溢出）
	 */
	@Override
	public void add(long value) {
		// 第一步：校验参数是否在int范围内，超范围抛异常
		int addInt = Math.toIntExact(value);
		// 第二步：精确累加，结果超范围抛异常
		this.value = Math.addExact(this.value, addInt);
	}

	/**
	 * 将当前int值转为{@link BigDecimal}（无精度丢失）
	 * <p>
	 * 转换逻辑：通过{@link BigDecimal#BigDecimal(int)}直接构造，
	 * 数值与当前{@link #value}完全一致（如value=123→BigDecimal(123.0)），无任何精度偏差；
	 * <p>
	 * 适用场景：需与{@link BigDecimalValue}（高精度小数）进行运算，或需保留小数位的场景（如金额计算中的单位转换）。
	 *
	 * @return 与当前值等价的{@link BigDecimal}实例（非null，无精度丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(value);
	}

	/**
	 * 将当前int值转为{@link BigInteger}（范围扩展）
	 * <p>
	 * 转换逻辑：通过字符串拼接（{@code "" + value}）构造BigInteger，
	 * 等价于更高效的{@code BigInteger.valueOf(value)}（推荐优化）；
	 * <p>
	 * 作用：突破int范围限制，支持后续与超大整数（如{@link BigIntegerValue}）的运算。
	 *
	 * @return 与当前值等价的{@link BigInteger}实例（非null，范围无限制）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return new BigInteger("" + value);
	}

	/**
	 * 获取当前原生int值（无任何转换开销，效率最高）
	 * <p>
	 * 特性：直接返回{@link #value}字段，无类型转换、无对象创建，是所有get方法中性能最优的；
	 * <p>
	 * 适用场景：原生int运算（如数组索引{@code arr[getAsInt()]}）、普通数值判断（{@code getAsInt() > 0}）。
	 *
	 * @return 当前存储的原生int数值（范围：-2147483648~2147483647）
	 */
	@Override
	public int getAsInt() {
		return value;
	}

	/**
	 * 比较当前int值与目标{@link Value}的大小（支持目标值超int范围）
	 * <p>
	 * 比较规则（分场景优先级）： 1.
	 * 若目标{@code o}非数值类型（{@link Value#isNumber()}为false）：委托父类{@link NumberAdder#compareTo(Value)}处理；
	 * 2. 若目标{@code o}是数值类型： a. 提取目标的{@link BigInteger}值（突破int范围，统一比较标准）； b. 目标值
	 * &gt; {@link Integer#MAX_VALUE} → 当前int值更小，返回-1； c. 目标值 &lt;
	 * {@link Integer#MIN_VALUE} → 当前int值更大，返回1； d. 目标值在int范围内 →
	 * 调用{@link Integer#compare(int, int)}比较原生int值，返回结果（负整数=当前小，0=相等，正整数=当前大）。
	 *
	 * @param o 待比较的{@link Value}对象（可为null，null视为小于任何数值类型）
	 * @return int：比较结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）
	 */
	@Override
	public int compareTo(Value o) {
		if (o.isNumber()) {
			BigInteger otherValue = o.getAsBigInteger();
			// 场景1：目标值超int上限 → 当前值小
			if (otherValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				return -1;
			}
			// 场景2：目标值超int下限 → 当前值大
			if (otherValue.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
				return 1;
			}
			// 场景3：目标值在int范围内 → 原生int比较
			return Integer.compare(this.value, otherValue.intValue());
		}
		// 非数值类型 → 父类默认逻辑
		return super.compareTo(o);
	}

	/**
	 * 将当前int值转为十进制字符串（原生格式，无多余符号）
	 * <p>
	 * 转换逻辑：委托{@link Integer#toString(int)}实现，输出格式与原生int一致： - 正数（如42）→ "{@code 42}"；
	 * - 负数（如-123）→ "{@code -123}"； - 零（0）→ "{@code 0}"；
	 * <p>
	 * 适用场景：日志打印、前端页面展示、JSON序列化（避免科学计数法）。
	 *
	 * @return 当前int值的十进制字符串（非null，无多余格式符）
	 */
	@Override
	public String getAsString() {
		return Integer.toString(value);
	}

	/**
	 * 计算当前实例的哈希值（基于原生int值，符合equals-hashCode契约）
	 * <p>
	 * 逻辑：直接委托{@link Integer#hashCode(int)}，基于当前{@link #value}计算哈希值；
	 * <p>
	 * 契约保障：若后续重写{@link #equals(Object)}，需确保“equals为true的实例，hashCode必相等”（当前实现已满足此前提）。
	 *
	 * @return 基于当前int值的哈希值（int类型）
	 */
	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	/**
	 * 对当前int值取反（正数→负数，负数→正数，0→0）
	 * <p>
	 * 实现逻辑：
	 * 1. 若当前值为0：直接返回自身（避免创建新实例，提升复用效率）；
	 * 2. 若当前值非0：通过{@link Math#negateExact(int)}精确取反，确保溢出时抛出异常；
	 * <p>
	 * 关键防护：针对{@link Integer#MIN_VALUE}（-2147483648），其取反结果（2147483648）超出int范围，
	 * 会通过{@link Math#negateExact(int)}直接抛出{@link ArithmeticException}，避免原生取反导致的逻辑错误。
	 *
	 * @return 取反后的{@link IntValue}实例（非null，取反结果在int范围内时）
	 * @throws ArithmeticException 若取反结果超出int范围（仅当当前值为{@link Integer#MIN_VALUE}时触发）
	 */
	@Override
	public NumberValue negate() throws ArithmeticException {
		if (value == 0) {
			return this;
		}
		// 精确取反，溢出时抛异常（处理Integer.MIN_VALUE场景）
		return new IntValue(Math.negateExact(value));
	}
}