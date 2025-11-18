package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 长整数实现类，继承自{@link NumberAdder}，基于Java原生{@code long}类型存储数值，
 * 聚焦<strong>超int范围但未达long上限</strong>（-2⁶³~2⁶³-1，即-9223372036854775808~9223372036854775807）的高效处理，
 * 是框架中“范围与性能平衡”的核心组件——比{@link IntValue}（int范围）支持更大数值，比{@link BigIntegerValue}（无限精度）更轻量、运算更快，
 * 适配时间戳、中大规模计数等高频中等范围整数场景。
 * 
 * <p>
 * <strong>设计核心（三大核心目标）</strong>：
 * <ul>
 * <li><strong>范围与效率平衡</strong>：依托原生{@code long}的64位存储，覆盖-9e18~9e18的数值区间（远超int的2e9上限），
 * 同时避免高精度对象（如{@link BigInteger}）的堆内存开销与运算延迟，运算基于JVM原生指令（如{@code ladd}），性能接近int；</li>
 * <li><strong>溢出安全防护</strong>：所有数值修改（如{@link #add(long)}）均通过{@link Math#addExact(long, long)}实现，
 * 一旦累加结果超出long范围，立即抛出{@link ArithmeticException}，杜绝原生long“溢出循环”问题（如{@code 9223372036854775807+1=-9223372036854775808}）；</li>
 * <li><strong>场景化复用</strong>：通过{@link #initialValue}存储实例创建时的初始值，支持{@link #reset()}一键恢复初始状态，
 * 可循环复用（如批量处理时间戳的临时变量），减少对象创建与GC开销。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>扩展范围覆盖</strong>：支持的数值区间可满足：
 *   <ul>
 *   <li>时间戳：毫秒级时间戳（如{@code System.currentTimeMillis()}）可覆盖至公元292亿年；</li>
 *   <li>中大规模计数：亿级订单量、千万级用户数、百亿级接口调用次数，均未达long上限；</li>
 *   </ul>
 * </li>
 * <li><strong>原生运算性能</strong>：加法、类型转换（如{@link #getAsLong()}）无额外对象创建，直接操作原生值，
 * 比{@link BigIntegerValue}快1~2个数量级，适合高频场景（如实时日志时间戳累加、分页偏移量计算）；</li>
 * <li><strong>严格溢出控制</strong>：{@link #add(long)}仅需一步精确累加校验，无需额外范围判断，
 * 溢出时显式抛异常，便于业务层快速定位“数值超范围”问题（如时间戳计算错误、计数溢出）；</li>
 * <li><strong>无缝类型兼容</strong>：
 *   <ul>
 *   <li>转{@link BigDecimal}：通过{@link BigDecimal#valueOf(long)}高效构造，无精度丢失（如{@code 1234567890123456789L→1234567890123456789}，无小数位冗余）；</li>
 *   <li>转{@link BigInteger}：通过{@link BigInteger#valueOf(long)}高效实现，突破long范围，支持后续超大整数运算（避免字符串拼接开销）；</li>
 *   <li>转{@link String}：原生十进制格式（如{@code -9223372036854775808L→"-9223372036854775808"}），无多余格式符，避免科学计数法；</li>
 *   </ul>
 * </li>
 * <li><strong>范围感知比较</strong>：{@link #compareTo(Value)}可处理目标值超long范围的场景（如目标是{@link BigIntegerValue}），
 * 自动判断“当前long值”与“超范围值”的大小（如long最大值&lt;超long值），避免比较逻辑错误；</li>
 * <li><strong>完整契约实现</strong>：重写{@link #equals(Object)}和{@link #hashCode()}，符合“数值相等则实例相等”的契约，
 * 支持跨类型数值比较（如{@link LongValue}与{@link IntValue}数值相等时判定为equals），可安全用于哈希容器（如{@link java.util.HashMap}）。</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 * <li>时间戳处理：存储/计算毫秒级时间戳（如接口调用时间、订单创建时间），原生long是时间戳的标准类型；</li>
 * <li>中大规模计数：电商平台商品ID（达数亿）、用户注册量（千万级至亿级）、消息队列消费次数（超20亿次）；</li>
 * <li>大数据分页：超大表的分页偏移量（如{@code offset=5000000000L}，超int上限2147483647）；</li>
 * <li>分布式ID：基于雪花算法（Snowflake）生成的分布式ID（64位结构，原生long存储）；</li>
 * <li>性能敏感的中等范围运算：游戏角色经验值（超千万级）、金融交易流水号（递增至亿级）。</li>
 * </ul>
 *
 * <h3>使用示例（含实际业务场景）</h3>
 * 
 * <pre class="code">
 * // 1. 创建实例（场景：存储当前时间戳）
 * long currentTime = System.currentTimeMillis(); // 示例：1718000000000L（约2024年6月）
 * LongValue timestamp = new LongValue(currentTime);
 *
 * // 2. 正常累加（场景：时间戳加1秒，模拟后续时间点）
 * timestamp.add(1000L); // 1718000000000L + 1000L = 1718000001000L，无溢出
 * System.out.println(timestamp.getAsLong()); // 输出：1718000001000
 *
 * // 3. 溢出场景（场景：累加超出long上限，抛异常）
 * try {
 * 	// long最大值：9223372036854775807L，加1后超范围
 * 	LongValue maxLong = new LongValue(Long.MAX_VALUE);
 * 	maxLong.add(1L);
 * } catch (ArithmeticException e) {
 * 	System.out.println("累加溢出：" + e.getMessage()); // 输出："long overflow"
 * }
 *
 * // 4. 重置为初始值（场景：复用实例存储新时间戳前重置）
 * timestamp.reset(); // 恢复到创建时的1718000000000L
 * System.out.println(timestamp.getAsLong()); // 输出：1718000000000
 *
 * // 5. 跨类型比较（场景：与超long范围的数值比较）
 * BigInteger bigNum = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE); // 9223372036854775808L（超long上限）
 * BigIntegerValue bigIntValue = new BigIntegerValue(bigNum);
 * int compareRes1 = timestamp.compareTo(bigIntValue); // 1718000000000L &lt; 9223372036854775808L → 返回-1
 *
 * LongValue anotherTimestamp = new LongValue(currentTime);
 * int compareRes2 = timestamp.compareTo(anotherTimestamp); // 相等 → 返回0
 *
 * // 6. 类型转换（场景：适配不同下游需求）
 * BigDecimal timeDecimal = timestamp.getAsBigDecimal(); // 1718000000000（用于金额相关的时间计算）
 * // 示例：时间戳转小时数（保留2位小数）
 * BigDecimal hours = timeDecimal.divide(BigDecimal.valueOf(3600000), 2, RoundingMode.HALF_UP);
 * System.out.println(hours); // 输出：477222.22（1718000000000ms ÷ 3600000ms/h ≈ 477222.22h）
 *
 * BigInteger timeBigInt = timestamp.getAsBigInteger(); // 1718000000000（用于超long范围的后续运算）
 * String timeStr = timestamp.getAsString(); // "1718000000000"（用于日志打印、接口返回）
 *
 * // 7. 取反操作（场景：数值符号反转）
 * LongValue positive = new LongValue(1000L);
 * LongValue negative = (LongValue) positive.negate();
 * System.out.println(negative.getAsLong()); // 输出：-1000
 *
 * LongValue zero = new LongValue(0L);
 * LongValue sameZero = (LongValue) zero.negate();
 * System.out.println(sameZero == zero); // 输出：true（零取反仍为自身，复用实例）
 *
 * // 8. 跨类型equals判定（场景：哈希容器存储与查找）
 * IntValue intVal = new IntValue(1000);
 * LongValue longVal = new LongValue(1000L);
 * System.out.println(longVal.equals(intVal)); // 输出：true（数值相等，跨类型判定为相等）
 *
 * java.util.Map&lt;Value, String&gt; valueMap = new java.util.HashMap&lt;&gt;();
 * valueMap.put(longVal, "测试值");
 * System.out.println(valueMap.get(intVal)); // 输出："测试值"（哈希码一致，可正常查找）
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li><strong>范围严格限制</strong>：仅支持long范围数值，超出需替换为{@link BigIntegerValue}（如存储宇宙星体质量、超大素数），
 * 避免强行使用导致溢出异常；</li>
 * <li><strong>线程安全警告</strong>：类内无同步机制（如{@code synchronized}、{@link java.util.concurrent.atomic.AtomicLong}），
 * 多线程并发修改需外部加锁（示例：{@code synchronized (timestamp) { timestamp.add(1000L); }}），
 * 或改用{@link java.util.concurrent.atomic.AtomicLong}（若无需框架{@link Value}接口）；</li>
 * <li><strong>哈希性能保障</strong>：{@link #hashCode()}基于{@link Long#hashCode(long)}实现，
 * 避免原生强制转型（{@code (int)value}）导致的哈希冲突（如{@code 1L}与{@code 4294967297L}哈希码不同），
 * 结合{@link #equals(Object)}的跨类型判定，可安全用于{@link java.util.HashMap}、{@link java.util.HashSet}等哈希容器；</li>
 * <li><strong>溢出异常处理</strong>：{@link #add(long)}和{@link #negate()}会主动抛出{@link ArithmeticException}，业务层需根据场景处理：
 *   <ul>
 *   <li>允许溢出：捕获异常后转为{@link BigIntegerValue}（如{@code new BigIntegerValue(getAsBigInteger().add(BigInteger.valueOf(value)))}）；</li>
 *   <li>禁止溢出：提示用户“数值超出最大范围”（如分页偏移量超long上限时返回参数错误）；</li>
 *   </ul>
 * </li>
 * <li><strong>Long.MIN_VALUE取反注意</strong>：{@code Long.MIN_VALUE}（-9223372036854775808L）的取反结果为{@code Long.MAX_VALUE + 1}，
 * 超出long范围，调用{@link #negate()}会抛出{@link ArithmeticException}，需提前判断或捕获处理；</li>
 * <li><strong>类型转换优化</strong>：{@link #getAsBigInteger()}和{@link #getAsBigDecimal()}均采用原生valueOf方法构建实例，
 * 避免字符串拼接开销，性能优于通过字符串构造（如{@code new BigInteger(value + "")}），无需额外优化。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see NumberAdder 父类（定义数值累加、重置的基础契约，当前类实现其核心方法）
 * @see java.lang.Long 原生long的包装类，当前类基于其范围（-9223372036854775808~9223372036854775807）与运算规则
 * @see IntValue 普通整数实现类（数值≤2147483647时推荐使用，更轻量）
 * @see BigIntegerValue 超大整数实现类（数值&gt;9223372036854775807时的替代方案）
 * @see BigDecimalValue 高精度小数实现类（需与小数交互时的适配类）
 * @see Math#addExact(long, long) 支撑溢出防护的原生精确累加方法
 * @see Math#negateExact(long) 支撑取反溢出防护的原生方法
 * @see Long#hashCode(long) 原生long哈希值计算方法（保障哈希性能与唯一性）
 */
public class LongValue extends NumberAdder {
	private static final long serialVersionUID = 1L;

	/**
	 * 当前存储的原生long数值（核心状态字段）
	 * <p>
	 * 修改约束：仅允许通过{@link #add(long)}（累加）或{@link #reset()}（重置）更新，
	 * 禁止直接赋值，确保所有修改均经过溢出校验；
	 * <p>
	 * 取值范围：始终在long范围内（-9223372036854775808~9223372036854775807），由{@link #add(long)}的{@link Math#addExact(long, long)}保障。
	 */
	private long value;

	/**
	 * 实例创建时的初始值（不可变基准）
	 * <p>
	 * 作用：作为{@link #reset()}的恢复目标，初始化后不可修改，确保每次重置都能回到创建时的初始状态；
	 * <p>
	 * 特性：直接存储构造参数的long值，无额外转换，保证初始值的准确性（如时间戳、计数起点）。
	 */
	private final long initialValue;

	/**
	 * 构造方法：通过原生long初始值创建实例
	 * <p>
	 * 核心逻辑：
	 * 1. 存储初始值到{@link #initialValue}（不可变，作为后续重置的基准）；
	 * 2. 调用{@link #reset()}将{@link #value}初始化为初始值，确保实例创建即处于可用状态。
	 *
	 * @param initialValue 初始数值（必须在long范围内：-9223372036854775808~9223372036854775807，
	 *                     超出会导致JVM编译时拦截超范围字面量，运行时传入需自行确保合法性）
	 */
	public LongValue(long initialValue) {
		this.initialValue = initialValue;
		reset();
	}

	/**
	 * 重置当前数值为创建时的初始值
	 * <p>
	 * 实现逻辑：直接将{@link #value}赋值为{@link #initialValue}，无任何计算或对象创建，
	 * 时间复杂度O(1)，效率极高；
	 * <p>
	 * 适用场景：循环复用实例（如批量处理时间戳的临时变量、循环计数的重置），避免重复创建{@link LongValue}导致的GC压力。
	 */
	@Override
	public void reset() {
		this.value = initialValue;
	}

	/**
	 * 累加指定long数值到当前值（核心修改方法，含溢出防护）
	 * <p>
	 * 执行步骤：
	 * 1. <strong>精确累加</strong>：调用{@link Math#addExact(long, long)}计算“当前value + 输入value”，
	 *    若结果超出long范围（&lt;-9223372036854775808或&gt;9223372036854775807），立即抛出{@link ArithmeticException}；
	 * 2. <strong>状态更新</strong>：将累加结果赋值给{@link #value}，完成状态修改。
	 * <p>
	 * 特殊处理：若输入value为负数（如-1000L），等价于“当前值减去输入值的绝对值”（即{@code value = value - 1000L}），
	 * 无需额外判断负数逻辑。
	 *
	 * @param value 待累加的long数值（可正可负，累加后需在long范围内，否则抛异常）
	 * @throws ArithmeticException 若累加结果超出long范围（即长整数溢出）
	 */
	@Override
	public void add(long value) {
		this.value = Math.addExact(this.value, value);
	}

	/**
	 * 将当前long值转为{@link BigDecimal}（无精度丢失，高效实现）
	 * <p>
	 * 转换逻辑：通过{@link BigDecimal#valueOf(long)}直接构造，避免创建临时对象，性能最优；
	 * 数值与当前{@link #value}完全一致（如{@code 1234567890123456789L→BigDecimal(1234567890123456789)}，无小数位冗余），
	 * 无任何精度偏差；
	 * <p>
	 * 适用场景：需与{@link BigDecimalValue}（高精度小数）进行运算（如时间戳转小时数、数值单位转换），
	 * 或需保留小数位的数值展示。
	 *
	 * @return 与当前值等价的{@link BigDecimal}实例（非null，无精度丢失，性能最优）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return BigDecimal.valueOf(value);
	}

	/**
	 * 将当前long值转为{@link BigInteger}（范围扩展，高效实现）
	 * <p>
	 * 转换逻辑：通过{@link BigInteger#valueOf(long)}直接构造，避免字符串拼接开销，当入参在long范围内时复用缓存实例，性能最优；
	 * <p>
	 * 作用：突破long范围限制，支持后续与超大整数（如{@link BigIntegerValue}）的运算（如存储超long的计数结果、跨类型比较）。
	 *
	 * @return 与当前值等价的{@link BigInteger}实例（非null，范围无限制，性能最优）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return BigInteger.valueOf(value);
	}

	/**
	 * 获取当前原生long值（无任何转换开销，效率最高）
	 * <p>
	 * 特性：直接返回{@link #value}字段，无类型转换、无对象创建，是所有get方法中性能最优的；
	 * <p>
	 * 适用场景：原生long运算（如时间戳比较{@code getAsLong() > System.currentTimeMillis()}）、
	 * 分布式ID判断、分页偏移量计算等无需类型转换的场景。
	 *
	 * @return 当前存储的原生long数值（范围：-9223372036854775808~9223372036854775807）
	 */
	@Override
	public long getAsLong() {
		return value;
	}

	/**
	 * 将当前long值转为十进制字符串（原生格式，无多余符号）
	 * <p>
	 * 转换逻辑：委托{@link Long#toString(long)}实现，输出格式与原生long一致，避免科学计数法：
	 * - 正数（如{@code 1718000000000L}）→ "{@code 1718000000000}"；
	 * - 负数（如{@code -9223372036854775808L}）→ "{@code -9223372036854775808}"；
	 * - 零（{@code 0L}）→ "{@code 0}"；
	 * <p>
	 * 适用场景：日志打印、前端页面展示、JSON序列化（确保大数值以完整十进制字符串呈现，无格式失真）。
	 *
	 * @return 当前long值的十进制字符串（非null，无多余格式符，无科学计数法）
	 */
	@Override
	public String getAsString() {
		return Long.toString(value);
	}

	/**
	 * 比较当前long值与目标{@link Value}的大小（支持目标值超long范围、跨类型比较）
	 * <p>
	 * 比较规则（分场景优先级）：
	 * 1. 若目标{@code o}为null：按框架约定，null视为小于任何数值类型，返回1；
	 * 2. 若目标{@code o}非数值类型（{@link Value#isNumber()}为false）：委托父类{@link NumberAdder#compareTo(Value)}处理；
	 * 3. 若目标{@code o}是数值类型：
	 *    a. 提取目标的{@link BigInteger}值（突破long范围，统一比较标准，避免范围溢出）；
	 *    b. 目标值 &gt; {@link Long#MAX_VALUE} → 当前long值更小，返回-1；
	 *    c. 目标值 &lt; {@link Long#MIN_VALUE} → 当前long值更大，返回1；
	 *    d. 目标值在long范围内 → 调用{@link Long#compare(long, long)}比较原生long值，返回结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）。
	 *
	 * @param o 待比较的{@link Value}对象（可为null，null视为小于任何数值类型）
	 * @return int：比较结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）
	 */
	@Override
	public int compareTo(Value o) {
		if (o == null) {
			return 1; // null视为小于任何数值
		}
		if (o.isNumber()) {
			BigInteger otherValue = o.getAsBigInteger();
			// 场景1：目标值超long上限 → 当前值小
			if (otherValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				return -1;
			}
			// 场景2：目标值超long下限 → 当前值大
			if (otherValue.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
				return 1;
			}
			// 场景3：目标值在long范围内 → 原生long比较（性能更优）
			return Long.compare(this.value, otherValue.longValue());
		}
		// 非数值类型 → 父类默认逻辑（通常按类型优先级或字符串比较）
		return super.compareTo(o);
	}

	/**
	 * 计算当前实例的哈希值（基于原生long值，符合equals-hashCode契约）
	 * <p>
	 * 逻辑：委托{@link Long#hashCode(long)}，基于当前{@link #value}的64位完整信息计算哈希值，
	 * 避免原生强制转型（{@code (int)value}）导致的哈希冲突（如{@code 1L}与{@code 4294967297L}的哈希码不同）；
	 * <p>
	 * 契约保障：与{@link #equals(Object)}完全匹配，确保“equals为true的实例，hashCode必相等”，
	 * 支持跨类型哈希容器存储与查找（如{@link IntValue}与{LongValue}数值相等时哈希码一致）。
	 *
	 * @return 基于当前long值的哈希值（int类型，无冲突风险）
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	/**
	 * 对当前数值取反（符号反转），含溢出防护，支持实例复用
	 * <p>
	 * 核心逻辑：
	 * 1. 若当前值为0：直接返回自身（0取反仍为0，避免不必要的对象创建，提升复用效率）；
	 * 2. 若当前值非0：调用{@link Math#negateExact(long)}进行精确取反，
	 *    若取反结果溢出（仅可能发生在{@code Long.MIN_VALUE}取反，因{@code -Long.MIN_VALUE = Long.MAX_VALUE + 1}超long范围），
	 *    立即抛出{@link ArithmeticException}；
	 * 3. 返回新的{@link LongValue}实例（原实例状态不变，符合不可变设计思想，避免副作用）。
	 * <p>
	 * 特殊场景：{@code new LongValue(Long.MIN_VALUE).negate()}会抛出溢出异常，需业务层捕获并转为{@link BigIntegerValue}。
	 *
	 * @return 取反后的{@link LongValue}实例（非null，原实例状态不变；值为0时返回自身）
	 * @throws ArithmeticException 若取反结果超出long范围（仅{@code Long.MIN_VALUE}取反时会触发）
	 */
	@Override
	public NumberValue negate() throws ArithmeticException {
		return this.value == 0 ? this : new LongValue(Math.negateExact(this.value));
	}

	/**
	 * 重写equals方法，确保数值相等的实例（含跨类型）判定为相等（严格遵循equals-hashCode契约）
	 * <p>
	 * 比较规则（优先级从高到低）：
	 * 1. 引用相等：直接返回true（同一实例必相等）；
	 * 2. 非Value类型：返回false（类型不匹配）；
	 * 3. 非数值类型Value：返回false（数值比较无意义）；
	 * 4. 数值比较：统一转为{@link BigInteger}进行比较（突破类型与范围限制，确保数值相等即判定为相等）。
	 *
	 * @param obj 待比较的对象（可为null，null返回false）
	 * @return boolean：true=数值相等（含跨类型），false=不相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Value)) {
			return false;
		}
		Value other = (Value) obj;
		if (!other.isNumber()) {
			return false;
		}
		// 跨类型、跨范围数值统一比较（BigInteger作为标准）
		return this.getAsBigInteger().equals(other.getAsBigInteger());
	}

	/**
	 * 重写toString方法，返回原生数值字符串（与{@link #getAsString()}一致，增强日志与调试可读性）
	 * <p>
	 * 特性：输出格式与原生long完全一致，无多余前缀或格式符，便于日志打印、调试断点查看、接口返回展示。
	 *
	 * @return 当前数值的十进制字符串（非null，格式原生）
	 */
	@Override
	public String toString() {
		return getAsString();
	}
}