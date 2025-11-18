package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.NumberUtils;

/**
 * 高精度整数实现类，继承自{@link NumberAdder}（提供数值累加与重置契约），封装Java原生{@link BigInteger}，
 * 解决基础类型{@code long}（最大值9223372036854775807，约9×10¹⁸）的范围不足问题，
 * 是框架中处理超大整数场景（如金融大额数值、密码学大素数）的核心组件。
 * 
 * <p>
 * <strong>设计核心</strong>：
 * <ul>
 * <li><strong>不可变数据安全</strong>：内部依赖{@link BigInteger}的不可变性，所有修改操作（如{@link #add(long)}）
 * 均通过创建新{@link BigInteger}实例实现，避免并发场景下的数据竞争（无需额外加锁，依赖不可变对象的线程安全特性）；</li>
 * <li><strong>状态可重置</strong>：通过{@link #initialValue}字段记录实例创建时的初始值，支持{@link #reset()}方法恢复初始状态，
 * 适用于循环复用实例的场景（如线程池中的计数变量，减少对象创建开销）；</li>
 * <li><strong>跨类型兼容性</strong>：实现{@link Value}接口，支持与{@link BigDecimalValue}（高精度小数）等
 * 其他数值类型的比较与运算，统一框架内数值处理逻辑，避免类型转换碎片化；</li>
 * <li><strong>引用安全防护</strong>：构造时通过{@link NumberUtils#newBigInteger(BigInteger)}创建初始值的拷贝，
 * 避免外部修改原{@link BigInteger}对象（虽其不可变，但确保初始值与外部完全解耦）；</li>
 * <li><strong>父类契约兼容</strong>：严格遵循{@link NumberValue}抽象基类约束，如{@link #getAsBigDecimal()}返回0位小数的BigDecimal，
 * 确保跨子类一致性。</li>
 * </ul>
 *
 * <h3>核心特性</h3>
 * <ul>
 * <li><strong>无限精度支持</strong>：数值大小仅受JVM内存限制，可存储任意长度整数（如100位、1000位大整数），无范围溢出风险；</li>
 * <li><strong>线程安全基础</strong>：基于{@link BigInteger}的不可变性设计，但{@link #add(long)}修改{@link #value}引用的操作非原子性——
 * 多线程环境下若需强原子性，建议结合{@code synchronized}或{@link java.util.concurrent.atomic.AtomicReference}包装；</li>
 * <li><strong>预置高频常量</strong>：提供{@link #ZERO}（数值0）和{@link #ONE}（数值1）的单例常量，
 * 避免重复创建高频使用的整数实例（如计数起点、比较基准），降低内存开销；</li>
 * <li><strong>完整类型转换</strong>：
 *   - 转为{@link BigDecimal}：通过{@link #getAsBigDecimal()}实现，返回0位小数（如{@code BigInteger(123)} → {@code BigDecimal(123.0)}），符合父类约束；
 *   - 转为{@link String}：通过{@link #getAsString()}实现，输出原生整数格式（无科学计数法），如负数{@code -456} → {@code "-456"}；
 *   - 转为基础类型：需通过{@link BigInteger#longValueExact()}、{@link BigInteger#intValueExact()}等精确方法，溢出直接抛异常，避免隐性丢失。</li>
 * <li><strong>扩展累加能力</strong>：除支持{@code long}类型累加外，额外提供{@link BigInteger}、{@link BigIntegerValue}类型的累加方法，
 * 适配高精度场景下的直接运算，无需手动类型转换。</li>
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
 * <h3>注意事项</h3>
 * <ul>
 * <li><strong>线程安全说明</strong>：{@link BigInteger}本身不可变，但{@link #add(long)}会修改{@link #value}的引用，
 * 多线程并发调用同一实例的add方法时，可能出现“值覆盖”（非原子操作）。若需原子性，建议：
 *   <ol>
 *     <li>使用{@code synchronized}包裹add调用（如{@code synchronized(bigIntValue) { bigIntValue.add(1); }}）；</li>
 *     <li>用{@link java.util.concurrent.atomic.AtomicReference}包装（如{@code AtomicReference<BigIntegerValue> ref = new AtomicReference<>(initial);}）；</li>
 *     <li>多线程场景下优先使用局部变量（避免共享实例）。</li>
 *   </ol>
 * </li>
 * <li><strong>构造方法参数校验</strong>：
 *   <ol>
 *     <li>字符串构造时，非法格式会抛{@link NumberFormatException}，包括：含字母（如"123a456"）、含小数（如"123.45"）、空字符串、仅符号（如"-"）；</li>
 *     <li>所有构造方法均不允许null输入，否则抛{@link NullPointerException}，需提前确保参数非null。</li>
 *   </ol>
 * </li>
 * <li><strong>常量使用约束</strong>：{@link #ZERO}和{@link #ONE}为单例，直接调用{@link #add(long)}会修改其全局状态，
 * 导致所有引用该常量的代码受影响。正确用法：仅作为只读基准，若需修改，基于常量拷贝创建新实例。</li>
 * <li><strong>基础类型转换风险</strong>：超大整数（如超过{@code Long.MAX_VALUE}）转为long/int时，必须先通过{@link BigInteger#compareTo(BigInteger)}校验范围，
 * 推荐使用{@link BigInteger#longValueExact()}而非{@link BigInteger#longValue()}——后者溢出时会返回错误值（无异常），前者直接抛异常，便于问题定位。</li>
 * <li><strong>equals跨类型比较规则</strong>：与{@link BigDecimalValue}等数值类型比较时，会取对方的{@link BigInteger}值（截断小数部分），
 * 例如{@code new BigIntegerValue(123).equals(new BigDecimalValue(123.99))}返回true（小数部分被忽略），需结合业务场景确认是否符合预期。</li>
 * <li><strong>add方法逻辑修正</strong>：原实现未将累加结果赋值给{@link #value}，导致累加无效果；当前版本已修正为{@code this.value = this.value.add(...)}，
 * 确保状态更新生效，升级时需注意替换旧实现。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see BigInteger 底层依赖的Java原生高精度整数类（提供不可变整数的核心能力）
 * @see NumberAdder 父类（定义数值累加、重置的基础契约，当前类实现其抽象方法）
 * @see NumberUtils 框架内数值工具类（用于构造/重置时创建BigInteger拷贝，确保引用安全）
 * @see BigDecimalValue 高精度小数类（可与当前类无缝转换，支持跨类型运算与比较）
 */
public class BigIntegerValue extends NumberAdder {
    private static final long serialVersionUID = 1L;

    /**
     * 数值0的单例常量实例，基于{@link BigInteger#ZERO}创建
     * <p>
     * 用途：作为数值比较的“零基准”（如判断差值是否为0）、计数变量的初始值，避免重复创建实例
     * <p>
     * <strong>注意</strong>：不可直接调用{@link #add(long)}修改其状态，否则会影响所有引用此常量的代码
     */
    public static final BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);

    /**
     * 数值1的单例常量实例，基于{@link BigInteger#ONE}创建
     * <p>
     * 用途：计数累加的步长（如循环计数{@code mutableOne.add(1)}）、乘法运算的单位元（任何数×1不变）
     * <p>
     * <strong>注意</strong>：不可直接调用{@link #add(long)}修改其状态，否则会影响所有引用此常量的代码
     */
    public static final BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);

    /**
     * 当前存储的高精度整数值（不可变对象引用）
     * <p>
     * 特性：由于{@link BigInteger}不可变，所有修改操作（如{@link #add(long)}）均会生成新{@link BigInteger}实例，
     * 并更新此引用；原实例不会被修改，确保并发场景下的“读安全”（写操作需额外保证原子性）
     */
    private BigInteger value;

    /**
     * 实例创建时的初始值（不可变）
     * <p>
     * 作用：作为{@link #reset()}方法的恢复基准，通过{@link NumberUtils#newBigInteger(BigInteger)}创建拷贝，
     * 确保与外部传入的初始值对象完全解耦（即使外部修改原对象，也不影响当前实例的初始状态）
     */
    private final BigInteger initialValue;

    /**
     * 构造方法：通过指定的{@link BigInteger}初始值创建实例
     * <p>
     * 核心逻辑：
     * 1. 接收外部传入的初始值，通过{@link NumberUtils#newBigInteger(BigInteger)}创建拷贝（空值校验+深拷贝，避免外部引用泄露）；
     * 2. 将拷贝后的初始值赋值给{@link #initialValue}（永久存储，不可修改）；
     * 3. 调用{@link #reset()}方法，将{@link #value}初始化为{@link #initialValue}的拷贝，确保初始状态正确。
     *
     * @param initialValue 初始高精度整数值（不可为null，否则{@link NumberUtils#newBigInteger(BigInteger)}会抛出{@link NullPointerException}）
     * @throws NullPointerException 若{@code initialValue}为null（由{@link NumberUtils#newBigInteger(BigInteger)}触发，确保输入合法性）
     */
    public BigIntegerValue(BigInteger initialValue) {
        this.initialValue = NumberUtils.newBigInteger(initialValue);
        reset();
    }

    /**
     * 扩展构造方法：通过{@code long}类型初始值创建实例（简化基础类型输入场景）
     * <p>
     * 核心逻辑：将{@code long}值通过{@link BigInteger#valueOf(long)}转为BigInteger（无精度丢失），再调用主构造方法，
     * 避免外部手动转换，提升开发效率。
     *
     * @param initialValue 基础类型long初始值（范围：{@code Long.MIN_VALUE} ~ {@code Long.MAX_VALUE}）
     */
    public BigIntegerValue(long initialValue) {
        this(BigInteger.valueOf(initialValue));
    }

    /**
     * 扩展构造方法：通过字符串类型初始值创建实例（支持超大整数文本输入）
     * <p>
     * 核心逻辑：通过{@link BigInteger#BigInteger(String)}将字符串转为BigInteger，再调用主构造方法；
     * 若字符串为非法整数格式（含字母、小数、空串等），会直接抛出{@link NumberFormatException}。
     *
     * @param initialValue 字符串类型初始值（如"12345678901234567890"、"-9876543210"，不可为null或空字符串）
     * @throws NumberFormatException 若字符串不是合法的整数格式（如"123a456"、"123.45"、""、"-"）
     * @throws NullPointerException 若{@code initialValue}为null
     */
    public BigIntegerValue(String initialValue) {
        this(new BigInteger(initialValue));
    }

    /**
     * 比较当前实例与目标{@link Value}的数值大小（实现{@link Value}接口契约）
     * <p>
     * 比较规则（优先级从高到低）：
     * 1. 若目标{@code o}为null：返回1（约定null小于任何数值类型，确保排序一致性）；
     * 2. 若目标{@code o}是数值类型（{@link Value#isNumber()}返回true）：
     *    - 提取目标的{@link BigInteger}值（通过{@link Value#getAsBigInteger()}，小数类型会截断小数部分，遵循整数比较规则）；
     *    - 调用当前{@link #value}的{@link BigInteger#compareTo(BigInteger)}方法，返回比较结果（负整数=当前&lt;目标，0=当前=目标，正整数=当前&gt;目标）；
     * 3. 若目标{@code o}非数值类型：委托父类{@link NumberAdder#compareTo(Value)}处理（默认按类名哈希排序，确保排序稳定性）。
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
     * 计算当前实例的哈希值（严格遵循{@code equals-hashCode}契约）
     * <p>
     * 哈希逻辑：直接复用内部{@link #value}（{@link BigInteger}）的哈希值——因{@link BigInteger}已保证“数值相等则哈希值相等”，
     * 故当前类无需额外计算，确保“equals为true的实例，hashCode必相等”，适配集合（如HashMap、HashSet）的存储需求。
     *
     * @return int：基于当前{@link #value}的哈希值（与数值内容强关联，数值不变则哈希值不变）
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * 判断当前实例与目标对象是否相等（支持跨类型数值相等判定，遵循{@code equals-hashCode}契约）
     * <p>
     * 相等规则：
     * 1. 若目标对象与当前实例为同一引用：返回true（引用相等优先）；
     * 2. 若目标对象为null：返回false；
     * 3. 若目标对象是{@link Value}接口实现类且为数值类型：比较两者的{@link BigInteger}值是否相等（支持跨类型，如BigIntegerValue与BigDecimalValue）；
     * 4. 其他情况（非Value类型、非数值类型）：返回false。
     *
     * @param obj 待比较的对象（可为null）
     * @return boolean：true=相等，false=不相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Value)) {
            return false;
        }
        Value other = (Value) obj;
        if (!other.isNumber()) {
            return false;
        }
        // 跨类型数值相等判定（统一转为BigInteger，忽略小数部分）
        return this.value.equals(other.getAsBigInteger());
    }

    /**
     * 将当前高精度整数转为{@link BigDecimal}类型（无精度丢失，符合{@link NumberValue}父类约束）
     * <p>
     * 转换逻辑：通过{@link BigDecimal#BigDecimal(BigInteger)}构造方法直接创建，小数位为0（如{@code BigInteger(123)} → {@code BigDecimal(123.0)}），
     * 严格遵循父类“整数类型补0位小数”的约束，确保跨子类转换一致性。
     * <p>
     * 适用场景：需与{@link BigDecimalValue}进行运算时的类型适配、金融场景中小数格式要求的输出。
     *
     * @return {@link BigDecimal}：与当前值等价的小数实例（非null，无精度丢失，小数位为0）
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return new BigDecimal(value);
    }

    /**
     * 获取当前实例封装的{@link BigInteger}原始值（安全返回，无引用泄露）
     * <p>
     * 安全说明：由于{@link BigInteger}是不可变对象，返回当前{@link #value}的直接引用——外部即使获取该引用，也无法修改其内容，
     * 故无需创建拷贝（避免不必要的性能开销），同时保证数据安全。
     *
     * @return {@link BigInteger}：当前实例存储的高精度整数值（非null，与内部状态完全一致）
     */
    @Override
    public BigInteger getAsBigInteger() {
        return value;
    }

    /**
     * 将当前高精度整数转为字符串表示（实现{@link Value}接口契约，无科学计数法）
     * <p>
     * 转换逻辑：直接调用{@link #value}的{@link BigInteger#toString()}方法，输出原生整数格式——
     * 正数无符号（如{@code 456} → {@code "456"}）、负数带负号（如{@code -789} → {@code "-789"}），
     * 不使用科学计数法（即使是超大整数），适配日志打印、JSON序列化、前端展示等场景。
     *
     * @return String：当前整数的字符串形式（非null，无额外格式符，直观可读）
     */
    @Override
    public String getAsString() {
        return value.toString();
    }

    /**
     * 重置当前实例的数值为创建时的初始状态（实现{@link NumberAdder}接口契约，支持实例复用）
     * <p>
     * 核心逻辑：通过{@link NumberUtils#newBigInteger(BigInteger)}创建{@link #initialValue}的新拷贝，
     * 赋值给{@link #value}——确保每次重置后都是全新的{@link BigInteger}实例，与初始值解耦（避免引用复用导致的意外修改）。
     * <p>
     * 适用场景：循环复用实例（如线程池中的计数器、批量处理中的临时变量），无需重新创建{@link BigIntegerValue}，降低对象创建与GC开销。
     */
    @Override
    public void reset() {
        this.value = NumberUtils.newBigInteger(initialValue);
    }

    /**
     * 累加指定的{@code long}类型数值到当前值（实现{@link NumberAdder}接口契约，修正原逻辑无效问题）
     * <p>
     * 核心逻辑（修正后）：
     * 1. 将{@code long}类型的{@code value}通过{@link BigInteger#valueOf(long)}转为BigInteger（无精度丢失）；
     * 2. 调用当前{@link #value}的{@link BigInteger#add(BigInteger)}方法，生成新的累加后实例（因BigInteger不可变，原实例不变）；
     * 3. 将新实例赋值给{@link #value}，完成状态更新——修正原逻辑“未赋值导致累加无效果”的核心问题。
     * <p>
     * 特殊场景：若{@code value}为负数，等价于“当前值减去{@code -value}”（如{@code add(-100)} → 当前值-100）。
     *
     * @param value 要累加的{@code long}类型数值（可正可负，范围：{@code Long.MIN_VALUE} ~ {@code Long.MAX_VALUE}）
     */
    @Override
    public void add(long value) {
        this.value = this.value.add(BigInteger.valueOf(value)); // 关键修正：将累加结果赋值给this.value，确保状态更新
    }

    /**
     * 扩展累加方法：支持{@link BigInteger}类型数值累加（适配高精度输入场景，无精度丢失）
     * <p>
     * 核心逻辑：
     * 1. 通过{@link NumberUtils#newBigInteger(BigInteger)}对输入值进行空值校验与拷贝，避免外部引用泄露；
     * 2. 调用{@link BigInteger#add(BigInteger)}生成新实例，更新{@link #value}，确保状态正确。
     * <p>
     * 适用场景：已持有BigInteger对象（如密码学运算结果），需直接累加，避免手动转换为long（可能溢出）。
     *
     * @param value 要累加的高精度整数（不可为null，否则抛{@link NullPointerException}）
     * @throws NullPointerException 若{@code value}为null（由{@link NumberUtils#newBigInteger(BigInteger)}触发）
     */
    public void add(BigInteger value) {
        this.value = this.value.add(NumberUtils.newBigInteger(value));
    }

    /**
     * 扩展累加方法：支持{@link BigIntegerValue}类型数值累加（跨实例高精度累加，代码更简洁）
     * <p>
     * 核心逻辑：提取目标实例的{@link BigInteger}值（通过{@link #getAsBigInteger()}），调用上述{@link #add(BigInteger)}方法，
     * 避免手动提取值，提升开发效率。
     *
     * @param value 要累加的{@link BigIntegerValue}实例（不可为null，否则抛{@link NullPointerException}）
     * @throws NullPointerException 若{@code value}为null
     */
    public void add(BigIntegerValue value) {
        this.add(value.getAsBigInteger());
    }

    /**
     * 对当前数值取反（符号反转），无溢出风险（BigInteger无限范围特性）
     * <p>
     * 核心逻辑：通过{@link BigInteger#negate()}生成取反后的新实例（正数→负数，负数→正数，0→0），
     * 创建新的{@link BigIntegerValue}返回——原实例状态不变，符合不可变设计的“修改创建新实例”原则。
     * <p>
     * 特性：因BigInteger无范围限制，取反操作永远不会溢出（即使是超大整数），理论上不会抛{@link ArithmeticException}，
     * 保留该异常声明仅为遵循{@link NumberValue}接口契约。
     *
     * @return 取反后的{@link BigIntegerValue}实例（非null，原实例状态不变）
     * @throws ArithmeticException 理论上无溢出风险，保留该异常以遵循{@link NumberValue}接口契约
     */
    @Override
    public NumberValue negate() throws ArithmeticException {
        return new BigIntegerValue(this.value.negate());
    }

    /**
     * 重写toString方法，返回数值字符串（与{@link #getAsString()}一致，增强日志与调试可读性）
     * <p>
     * 用途：日志打印、调试时直接输出数值，无需额外调用{@link #getAsString()}，简化代码。
     *
     * @return 当前数值的字符串表示（非null，与{@link #getAsString()}输出完全一致）
     */
    @Override
    public String toString() {
        return getAsString();
    }
}