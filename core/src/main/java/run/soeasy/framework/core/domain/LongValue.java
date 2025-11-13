package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 长整数实现类，继承自{@link NumberAdder}，基于Java原生{@code long}类型存储数值，
 * 聚焦**超int范围但未达long上限**（-2⁶³~2⁶³-1，即-9223372036854775808~9223372036854775807）的高效处理，
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
 * <li><strong>扩展范围覆盖</strong>：支持的数值区间可满足： -
 * 时间戳：毫秒级时间戳（如{@code System.currentTimeMillis()}）可覆盖至公元292亿年； -
 * 中大规模计数：亿级订单量、千万级用户数、百亿级接口调用次数，均未达long上限；</li>
 * <li><strong>原生运算性能</strong>：加法、类型转换（如{@link #getAsLong()}）无额外对象创建，直接操作原生值，
 * 比{@link BigIntegerValue}快1~2个数量级，适合高频场景（如实时日志时间戳累加、分页偏移量计算）；</li>
 * <li><strong>严格溢出控制</strong>：{@link #add(long)}仅需一步精确累加校验，无需额外范围判断，
 * 溢出时显式抛异常，便于业务层快速定位“数值超范围”问题（如时间戳计算错误、计数溢出）；</li>
 * <li><strong>无缝类型兼容</strong>： -
 * 转{@link BigDecimal}：通过{@link BigDecimal#BigDecimal(long)}构造，无精度丢失（如{@code 1234567890123456789L→1234567890123456789.0}）；
 * -
 * 转{@link BigInteger}：突破long范围，支持后续超大整数运算（推荐优化为{@code BigInteger.valueOf(value)}，避免字符串拼接）；
 * -
 * 转{@link String}：原生十进制格式（如{@code -9223372036854775808L→"-9223372036854775808"}），无多余格式符；</li>
 * <li><strong>范围感知比较</strong>：{@link #compareTo(Value)}可处理目标值超long范围的场景（如目标是{@link BigIntegerValue}），
 * 自动判断“当前long值”与“超范围值”的大小（如long最大值&lt;超long值），避免比较逻辑错误。</li>
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
 * BigDecimal timeDecimal = timestamp.getAsBigDecimal(); // 1718000000000.0（用于金额相关的时间计算）
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
 * System.out.println(sameZero == zero); // 输出：true（零取反仍为自身）
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 * <li><strong>范围严格限制</strong>：仅支持long范围数值，超出需替换为{@link BigIntegerValue}（如存储宇宙星体质量、超大素数），
 * 避免强行使用导致溢出异常；</li>
 * <li><strong>线程安全警告</strong>：类内无同步机制（如{@code synchronized}、{@link java.util.concurrent.atomic.AtomicLong}），
 * 多线程并发修改需外部加锁（示例：{@code synchronized (timestamp) { timestamp.add(1000L); }}），
 * 或改用{@link java.util.concurrent.atomic.AtomicLong}（若无需框架{@link Value}接口）；</li>
 * <li><strong>哈希性能保障</strong>：当前{@link #hashCode()}基于{@link Long#hashCode(long)}实现，
 * 避免原生强制转型（{@code (int)value}）导致的哈希冲突（如{@code 1L}与{@code 4294967297L}哈希码不同），
 * 可安全用于哈希表（如{@link java.util.HashMap}）键；</li>
 * <li><strong>溢出异常处理</strong>：{@link #add(long)}和{@link #negate()}会主动抛出{@link ArithmeticException}，业务层需根据场景处理：
 * - 允许溢出：捕获异常后转为{@link BigIntegerValue}；
 * - 禁止溢出：提示用户“数值超出最大范围”；</li>
 * <li><strong>类型转换优化</strong>：{@link #getAsBigInteger()}当前实现为{@code new BigInteger(value + "")}，
 * 可优化为{@code BigInteger.valueOf(value)}（语义一致，避免字符串拼接，性能提升约30%），推荐项目编译前替换。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see NumberAdder 父类（定义数值累加、重置的基础契约，当前类实现其核心方法）
 * @see java.lang.Long
 *      原生long的包装类，当前类基于其范围（-9223372036854775808~9223372036854775807）与运算规则
 * @see IntValue 普通整数实现类（数值≤2147483647时推荐使用，更轻量）
 * @see BigIntegerValue 超大整数实现类（数值&gt;9223372036854775807时的替代方案）
 * @see BigDecimalValue 高精度小数实现类（需与小数交互时的适配类）
 * @see Math#addExact(long, long) 支撑溢出防护的原生精确累加方法
 * @see Math#negateExact(long) 支撑取反溢出防护的原生方法
 */
public class LongValue extends NumberAdder {
    private static final long serialVersionUID = 1L;

    /**
     * 当前存储的原生long数值（核心状态字段）
     * <p>
     * 修改约束：仅允许通过{@link #add(long)}（累加）或{@link #reset()}（重置）更新，
     * 禁止直接赋值，确保所有修改均经过溢出校验；
     * <p>
     * 取值范围：始终在long范围内，由{@link #add(long)}的{@link Math#addExact(long, long)}保障。
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
     * 核心逻辑： 1. 存储初始值到{@link #initialValue}（不可变，作为后续重置的基准）； 2.
     * 调用{@link #reset()}将{@link #value}初始化为初始值，确保实例创建即处于可用状态。
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
     * 实现逻辑：直接将{@link #value}赋值为{@link #initialValue}，无任何计算或对象创建， 时间复杂度O(1)，效率极高；
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
     * 执行步骤： 1. <strong>精确累加</strong>：调用{@link Math#addExact(long, long)}计算“当前value
     * + 输入value”，
     * 若结果超出long范围（&lt;-9223372036854775808或&gt;9223372036854775807），立即抛出{@link ArithmeticException}；
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
     * 将当前long值转为{@link BigDecimal}（无精度丢失）
     * <p>
     * 转换逻辑：通过{@link BigDecimal#BigDecimal(long)}直接构造，
     * 数值与当前{@link #value}完全一致（如{@code 1234567890123456789L→BigDecimal(1234567890123456789.0)}），
     * 无任何精度偏差；
     * <p>
     * 适用场景：需与{@link BigDecimalValue}（高精度小数）进行运算（如时间戳转小时数：{@code new BigDecimal(value).divide(new BigDecimal(3600000), 2, RoundingMode.HALF_UP)}），
     * 或需保留小数位的数值展示。
     *
     * @return 与当前值等价的{@link BigDecimal}实例（非null，无精度丢失）
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return new BigDecimal(value);
    }

    /**
     * 将当前long值转为{@link BigInteger}（范围扩展）
     * <p>
     * 转换逻辑：通过字符串拼接（{@code value + ""}）构造BigInteger，
     * 等价于更高效的{@code BigInteger.valueOf(value)}（推荐优化，避免字符串创建开销）；
     * <p>
     * 作用：突破long范围限制，支持后续与超大整数（如{@link BigIntegerValue}）的运算（如存储超long的计数结果）。
     *
     * @return 与当前值等价的{@link BigInteger}实例（非null，范围无限制）
     */
    @Override
    public BigInteger getAsBigInteger() {
        return new BigInteger(value + "");
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
     * 转换逻辑：委托{@link Long#toString(long)}实现，输出格式与原生long一致： -
     * 正数（如{@code 1718000000000L}）→ "{@code 1718000000000}"； -
     * 负数（如{@code -9223372036854775808L}）→ "{@code -9223372036854775808}"； -
     * 零（{@code 0L}）→ "{@code 0}"；
     * <p>
     * 适用场景：日志打印、前端页面展示、JSON序列化（避免科学计数法，如long大值不会显示为{@code 9.22E18}）。
     *
     * @return 当前long值的十进制字符串（非null，无多余格式符）
     */
    @Override
    public String getAsString() {
        return Long.toString(value);
    }

    /**
     * 比较当前long值与目标{@link Value}的大小（支持目标值超long范围）
     * <p>
     * 比较规则（分场景优先级）： 1.
     * 若目标{@code o}非数值类型（{@link Value#isNumber()}为false）：委托父类{@link NumberAdder#compareTo(Value)}处理；
     * 2. 若目标{@code o}是数值类型： a. 提取目标的{@link BigInteger}值（突破long范围，统一比较标准）； b. 目标值
     * &gt; {@link Long#MAX_VALUE} → 当前long值更小，返回-1； c. 目标值 &lt;
     * {@link Long#MIN_VALUE} → 当前long值更大，返回1； d. 目标值在long范围内 →
     * 调用{@link Long#compare(long, long)}比较原生long值，返回结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）。
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
            // 场景3：目标值在long范围内 → 原生long比较
            return Long.compare(this.value, otherValue.longValue());
        }
        // 非数值类型 → 父类默认逻辑（通常按类型优先级或字符串比较）
        return super.compareTo(o);
    }

    /**
     * 计算当前实例的哈希值（基于原生long值，符合equals-hashCode契约）
     * <p>
     * 逻辑：委托{@link Long#hashCode(long)}，基于当前{@link #value}的64位信息计算哈希值，
     * 避免原生强制转型（{@code (int)value}）导致的哈希冲突，确保不同long值（如{@code 1L}与{@code 4294967297L}）的哈希码不同；
     * <p>
     * 契约保障：若后续重写{@link #equals(Object)}，需确保“equals为true的实例，hashCode必相等”（当前实现已满足此前提）。
     *
     * @return 基于当前long值的哈希值（int类型）
     */
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    /**
     * 对当前数值取反（符号反转），含溢出防护
     * <p>
     * 核心逻辑： 1. 若当前值为0：直接返回自身（0取反仍为0，避免不必要的对象创建）； 2. 若当前值非0：调用{@link Math#negateExact(long)}进行精确取反，
     * 若取反结果溢出（仅可能发生在{@code Long.MIN_VALUE}取反，因{@code -Long.MIN_VALUE = Long.MAX_VALUE + 1}超long范围），
     * 立即抛出{@link ArithmeticException}； 3. 返回新的{@link LongValue}实例（原实例状态不变，符合不可变设计思想）。
     * <p>
     * 特殊场景：{@code new LongValue(Long.MIN_VALUE).negate()} 会抛出溢出异常，需业务层捕获并转为{@link BigIntegerValue}。
     *
     * @return 取反后的{@link LongValue}实例（非null，原实例状态不变）
     * @throws ArithmeticException 若取反结果超出long范围（仅{@code Long.MIN_VALUE}取反时会触发）
     */
    @Override
    public NumberValue negate() throws ArithmeticException {
        return this.value == 0 ? this : new LongValue(Math.negateExact(this.value));
    }

    /**
     * 重写equals方法，确保数值相等的实例判定为相等（符合equals-hashCode契约）
     * <p>
     * 比较规则： 1. 若引用相同：直接返回true； 2. 若类型不同：返回false； 3. 若为{LongValue}类型：比较当前{@link #value}是否相等；
     * 4. 若为其他数值类型（如{@link IntValue}、{@link BigIntegerValue}）：转换为{@link BigInteger}后比较（支持跨类型数值相等判定）。
     *
     * @param obj 待比较的对象（可为null）
     * @return boolean：true=相等，false=不相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Value)) {
            return false;
        }
        Value other = (Value)obj;
        if (!other.isNumber()) {
            return false;
        }
        // 跨类型数值比较（统一转为BigInteger避免范围问题）
        return this.getAsBigInteger().equals(other.getAsBigInteger());
    }

    /**
     * 重写toString方法，返回原生数值字符串（与{@link #getAsString()}一致，增强可读性）
     *
     * @return 当前数值的十进制字符串（非null）
     */
    @Override
    public String toString() {
        return getAsString();
    }
}