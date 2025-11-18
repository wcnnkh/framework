package run.soeasy.framework.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.comparator.NumberComparator;

/**
 * 高精度数值处理工具类（不可实例化），专注于 BigDecimal、BigInteger 及通用 Number 类型的核心操作，
 * 提供「尾部无效零清除、BigInteger 深拷贝、Number 转 BigDecimal/BigInteger/基础类型（无精度丢失）、
 * 精准数值比较、精准正负零判定、非法浮点校验、有效小数判定」八大核心功能， 彻底规避 Double/Float
 * 原生浮点精度陷阱，确保高精度数值处理的安全性、独立性、格式优化及精准性， 适用于金融计算、复杂数值运算、单位转换等对高精度数据有严格要求的业务场景。
 *
 * <p>
 * <strong>设计特性</strong>：
 * <ul>
 * <li><strong>不可实例化</strong>：通过 {@link UtilityClass}
 * 注解标记，自动生成私有构造方法，禁止创建实例，所有方法均为静态；</li>
 * <li><strong>安全可靠</strong>：关键方法通过 {@link NonNull} 注解强制非空校验，处理逻辑覆盖「null 值、0
 * 值、NaN、Infinity、自定义 Number 子类」等边界场景；</li>
 * <li><strong>高精度优先</strong>：核心逻辑基于 BigDecimal/BigInteger
 * 实现，toDouble/toFloat 已实现「范围校验+有效数字校验+反向兜底校验」三层防护，超出范围/精度自动抛异常，彻底杜绝隐性精度丢失，完全兼容 JDK 1.8+；</li>
 * <li><strong>格式优化</strong>：清除 BigDecimal
 * 尾部无效零，避免科学计数法，优化数值字符串展示（不改变数值本身）；</li>
 * <li><strong>引用独立</strong>：提供 BigInteger 深拷贝能力，避免多线程/反射场景下的引用共享风险；</li>
 * <li><strong>异常友好</strong>：所有异常均包含具体上下文信息（如类型名、数值、目标类型范围），便于问题定位；</li>
 * <li><strong>无额外依赖</strong>：仅依赖 JDK 1.8 原生类和 Lombok
 * 基础注解（{@link UtilityClass}、{@link NonNull}），无需第三方工具包支持。</li>
 * </ul>
 *
 * <p>
 * <strong>重要限制说明</strong>：
 * <ul>
 * <li>Float 类型仅能精确表示 ≤2^24（16777216）的整数及 ≤7 位有效数字的小数，超出则自动抛异常；</li>
 * <li>Double 类型仅能精确表示 ≤2^53（9007199254740992）的整数及 ≤17 位有效数字的小数，超出则自动抛异常；</li>
 * <li>int/long 转 Float/Double 时，需遵守上述精度阈值，超界会直接拦截，避免隐性精度丢失；</li>
 * <li>高精度场景建议优先使用 BigDecimal，避免依赖 Float/Double 存储超大整数或超高精度小数。</li>
 * </ul>
 *
 * <p>
 * <strong>核心功能映射</strong>：
 * <ul>
 * <li>格式优化：{@link #stripTrailingZeros(BigDecimal)}（清除 BigDecimal
 * 尾部无效零，规避科学计数法）；</li>
 * <li>引用安全：{@link #newBigInteger(BigInteger)}（BigInteger 深拷贝，独立引用）；</li>
 * <li>无精度转换：{@link #toBigDecimal(Number)}（Number →
 * BigDecimal）、{@link #toBigInteger(Number)}（Number → BigInteger）、
 * {@link #toLong(Number)}（Number → Long）、{@link #toInteger(Number)}（Number →
 * Integer）等（基础类型精准转换）；</li>
 * <li>精准比较：{@link #equals(Number, Number)}（跨 Number 类型精准比较，支持所有子类）；</li>
 * <li>精准判定：{@link #isZero(Number)}、{@link #isPositive(Number)}、{@link #isNegative(Number)}（正负零精准判定）；</li>
 * <li>非法值校验：{@link #validateLegalFloat(Number)}（拦截 NaN/Infinity 等非法浮点值）；</li>
 * <li>有效小数判定：{@link #isDecimalHasEffectiveFraction(Number)}（判断数值是否包含非零小数部分）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @see UtilityClass Lombok 注解，标记此类为不可实例化工具类（自动生成私有构造+静态方法）
 * @see BigDecimal Java 原生高精度小数类，工具类核心处理对象
 * @see BigInteger Java 原生高精度整数类，工具类核心处理对象
 * @see NonNull Lombok 注解，用于方法参数非空强制校验（null 时抛出 NullPointerException）
 * @see Number Java 数值类型顶层接口，支持所有数值子类（Integer、Long、Double、自定义 Number 等）
 * @see NumberComparator 数值比较器，提供跨类型精准比较能力
 */
@UtilityClass
public class NumberUtils {
    /** Float 精确表示整数的阈值（2^24） */
    private static final long FLOAT_EXACT_INT_THRESHOLD = 1L << 24;
    /** Double 精确表示整数的阈值（2^53） */
    private static final long DOUBLE_EXACT_LONG_THRESHOLD = 9007199254740992L;
    /** Float 最大有效数字位数（硬件存储限制） */
    private static final int FLOAT_MAX_EFFECTIVE_DIGITS = 7;
    /** Double 最大有效数字位数（硬件存储限制） */
    private static final int DOUBLE_MAX_EFFECTIVE_DIGITS = 17;

    /**
     * 清除 BigDecimal 尾部的无效零，优化数值字符串表示形式（不改变数值本身大小）
     * <p>
     * 核心应用场景：金融金额展示、数值日志输出、API 响应格式化（如 123.4500 → 123.45，100.000 → 100）。
     * <p>
     * 处理逻辑（按优先级）：
     * <ol>
     * <li>空值兼容：输入为 null 时直接返回 null，避免空指针异常；</li>
     * <li>0 值处理：数值为 0（signum() == 0）时返回 {@link BigDecimal#ZERO}，统一标准格式，规避科学计数法（如
     * 0E10 → 0）；</li>
     * <li>无需处理场景：无小数部分（scale() ≤ 0），直接返回原对象；</li>
     * <li>尾部零清除：调用 {@link BigDecimal#stripTrailingZeros()} 清除尾部无效零；</li>
     * <li>科学计数法规避：清除零后若变为整数（scale ≤ 0），通过 {@link BigDecimal#setScale(int)} 转为 0
     * 位小数格式（例：1E2 → 100）。</li>
     * </ol>
     * <p>
     * <strong>注意事项</strong>：BigDecimal 为不可变对象，此方法会返回处理后的新实例，需接收返回值方可生效，原输入对象不会被修改。
     *
     * @param number 待处理的 BigDecimal 实例（可为 null）
     * @return 清除尾部无效零后的 BigDecimal；输入为 null 时返回 null
     * @see BigDecimal#stripTrailingZeros() 原生尾部零清除方法
     * @see BigDecimal#setScale(int) 小数位数设置（用于规避科学计数法）
     */
    public static BigDecimal stripTrailingZeros(BigDecimal number) {
        if (number == null) {
            return null;
        }
        if (number.signum() == 0) {
            return BigDecimal.ZERO;
        }
        if (number.scale() <= 0) {
            return number;
        }

        BigDecimal stripped = number.stripTrailingZeros();
        if (stripped.scale() <= 0) {
            return stripped.setScale(0);
        }
        return stripped;
    }

    /**
     * 深拷贝 BigInteger 实例，确保新实例与原实例「数值一致、引用完全独立」，规避多线程/反射场景下的引用共享风险
     * <p>
     * 核心应用场景：需要修改 BigInteger 实例但需保留原始值、多线程并发操作同一数值副本、防止反射篡改原实例等场景。
     * <p>
     * 实现逻辑（保证深拷贝有效性）：
     * <ol>
     * <li>通过 {@link BigInteger#toByteArray()} 获取原实例的完整字节数组（包含数值符号、位数等所有信息）；</li>
     * <li>基于该字节数组通过 {@link BigInteger#BigInteger(byte[])} 构造新实例，确保数值与原实例完全一致；</li>
     * <li>新实例与原实例无任何引用关联，即使原实例被反射修改，拷贝实例也不受影响。</li>
     * </ol>
     *
     * @param bigInteger 待拷贝的 BigInteger 实例（不可为 null）
     * @return 深拷贝后的新 BigInteger 实例（数值与原实例一致，引用独立）
     * @throws NullPointerException 若输入 bigInteger 为 null（由 {@link NonNull} 注解自动触发）
     * @see BigInteger#toByteArray() 数值转字节数组（深拷贝核心依赖）
     * @see BigInteger#BigInteger(byte[]) 字节数组构造实例（深拷贝核心依赖）
     */
    public static BigInteger newBigInteger(@NonNull BigInteger bigInteger) {
        return new BigInteger(bigInteger.toByteArray());
    }

    /**
     * Number 转 BigDecimal（无精度丢失，兼容所有 Number 子类）
     * <p>
     * 核心优势：彻底规避 Double/Float 二进制精度陷阱（如 0.1d 直接转 BigDecimal 会失真，本方法通过 String 中转解决），
     * 针对不同类型优化转换效率（整数类型复用 JDK 缓存）。
     * <p>
     * 转换逻辑（按优先级）：
     * <ol>
     * <li>空值兼容：输入为 null 时返回 null；</li>
     * <li>非法浮点校验：调用 {@link #validateLegalFloat(Number)} 拦截 NaN/Infinity 等非法值；</li>
     * <li>直接类型转换：BigDecimal 实例直接返回，无额外开销；</li>
     * <li>高精度整数转换：BigInteger 转 BigDecimal（完整保留精度）；</li>
     * <li>基础/原子整数转换：Byte/Short/Integer/Long/AtomicInteger/AtomicLong 转 BigDecimal（复用 JDK 缓存，性能最优）；</li>
     * <li>浮点类型转换：Float/Double 通过 toString() 中转，避免二进制精度丢失（如 0.1f → "0.1" → 精准 BigDecimal）；</li>
     * <li>自定义子类兜底：其他 Number 子类通过 toString() 中转（需子类正确实现 toString() 为标准数值格式）。</li>
     * </ol>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 BigDecimal；输入为 null 时返回 null
     * @throws IllegalArgumentException 若输入为 Double/Float 类型的 NaN 或 Infinity
     * @throws NumberFormatException    若自定义 Number 子类的 toString() 非标准数值格式（如 "123a"）
     * @see #validateLegalFloat(Number) 非法浮点值前置校验
     */
    public static BigDecimal toBigDecimal(Number number) {
        if (number == null) {
            return null;
        }

        validateLegalFloat(number);

        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        } else if (number instanceof Byte || number instanceof Short || number instanceof Integer
                || number instanceof Long || number instanceof AtomicInteger || number instanceof AtomicLong) {
            return BigDecimal.valueOf(number.longValue());
        } else if (number instanceof Float) {
            Float floatVal = (Float) number;
            return new BigDecimal(Float.toString(floatVal));
        } else if (number instanceof Double) {
            Double doubleVal = (Double) number;
            return new BigDecimal(Double.toString(doubleVal));
        } else {
            return new BigDecimal(number.toString());
        }
    }

    /**
     * 跨 Number 类型精准比较两个数值是否相等（支持所有 Number 子类，无精度丢失）
     * <p>
     * 核心优势：解决不同 Number 子类 equals 方法不兼容问题（如 Integer(100).equals(Long(100)) → false，本方法返回 true）。
     * <p>
     * 比较逻辑（按优先级）：
     * <ol>
     * <li>空值判定：通过异或运算判断「一个 null、一个非 null」→ 直接返回 false；</li>
     * <li>双空判定：两个均为 null → 返回 true；</li>
     * <li>非法值校验：调用 {@link #validateLegalFloat(Number)} 校验两个数值是否为 NaN/Infinity（非法值直接抛出异常）；</li>
     * <li>精准比较：先通过原生 equals 判定（同类型且数值相等），再通过 {@link NumberComparator} 跨类型精准比较（解决不同子类兼容问题）。</li>
     * </ol>
     *
     * @param left  左侧待比较数值（可为 null）
     * @param right 右侧待比较数值（可为 null）
     * @return true=两个数值相等（忽略类型差异，仅比数值）；false=数值不相等或类型不兼容
     * @throws IllegalArgumentException 若任一数值为 Double/Float 类型的 NaN 或 Infinity
     * @see NumberComparator 跨类型数值比较器（核心依赖，确保精准比较）
     * @see #validateLegalFloat(Number) 非法浮点值校验（前置依赖）
     */
    public static boolean equals(Number left, Number right) {
        if (left == null ^ right == null) {
            return false;
        }
        if (left == null) {
            return true;
        }

        validateLegalFloat(left);
        validateLegalFloat(right);

        return left.equals(right) || NumberComparator.INSTANCE.compare(left, right) == 0;
    }

    /**
     * 精准判断数值是否为 0（支持所有 Number 子类，无精度丢失）
     * <p>
     * 核心逻辑：基于 {@link #equals(Number, Number)} 实现，将目标数值与 0（默认转为 Integer(0)）进行精准比较。
     * <p>
     * 特性：自动处理 null 值（返回 false）、非法浮点值（抛出异常），支持整数、小数、高精度类型的零值判定。
     *
     * @param number 待判定的数值（可为 null）
     * @return true=数值为 0（如 0、0.0、BigDecimal.ZERO、BigInteger.ZERO）；false=非 0 或 null
     * @throws IllegalArgumentException 若数值为 Double/Float 类型的 NaN 或 Infinity
     * @see #equals(Number, Number) 精准比较核心依赖
     */
    public static boolean isZero(Number number) {
        return equals(number, 0);
    }

    /**
     * 精准判断数值是否为正数（支持所有 Number 子类，无精度丢失）
     * <p>
     * 核心逻辑：非 null 校验 → 非法浮点值校验 → 通过 {@link NumberComparator} 判定数值大于 0。
     * <p>
     * 特性：正数定义为「严格大于 0」，不包含 0；自动过滤 null 值和非法浮点值。
     *
     * @param number 待判定的数值（可为 null）
     * @return true=数值为正数（如 1、0.1、BigDecimal.ONE、BigInteger.valueOf(100)）；false=非正数（含 null、0、负数）
     * @throws IllegalArgumentException 若数值为 Double/Float 类型的 NaN 或 Infinity
     * @see NumberComparator 精准比较核心依赖
     * @see #validateLegalFloat(Number) 非法浮点值校验
     */
    public static boolean isPositive(Number number) {
        if (number == null) {
            return false;
        }
        validateLegalFloat(number);
        return NumberComparator.INSTANCE.compare(number, 0) > 0;
    }

    /**
     * 精准判断数值是否为负数（支持所有 Number 子类，无精度丢失）
     * <p>
     * 核心逻辑：非 null 校验 → 非法浮点值校验 → 通过 {@link NumberComparator} 判定数值小于 0。
     * <p>
     * 特性：负数定义为「严格小于 0」，不包含 0；自动过滤 null 值和非法浮点值。
     *
     * @param number 待判定的数值（可为 null）
     * @return true=数值为负数（如 -1、-0.1、BigDecimal.valueOf(-10)）；false=非负数（含 null、0、正数）
     * @throws IllegalArgumentException 若数值为 Double/Float 类型的 NaN 或 Infinity
     * @see NumberComparator 精准比较核心依赖
     * @see #validateLegalFloat(Number) 非法浮点值校验
     */
    public static boolean isNegative(Number number) {
        if (number == null) {
            return false;
        }
        validateLegalFloat(number);
        return NumberComparator.INSTANCE.compare(number, 0) < 0;
    }

    /**
     * 校验 Number 是否为非法浮点值（NaN/Infinity），仅针对 Double/Float 类型（JDK 1.8+ 兼容）
     * <p>
     * 核心作用：为数值操作（比较、转换、运算、判定）提供前置非法值拦截，避免因 NaN/Infinity 导致的不可预期结果（如 NaN.equals(NaN) → false）。
     * <p>
     * 校验逻辑：
     * <ol>
     * <li>非浮点类型（int/long/BigDecimal/BigInteger 等）：天然无 NaN/Infinity，直接跳过校验；</li>
     * <li>Double 类型：通过 {@link Double#isNaN()}、{@link Double#isInfinite()} 校验；</li>
     * <li>Float 类型：通过 {@link Float#isNaN()}、{@link Float#isInfinite()} 校验；</li>
     * <li>非法值触发：校验通过则无动作，非法则抛出含具体值的异常。</li>
     * </ol>
     *
     * @param number 待校验的 Number 对象（可为 null，null 不触发校验，需调用方自行处理空值）
     * @throws IllegalArgumentException 若输入为 Double/Float 类型的 NaN 或 Infinity，异常信息包含具体非法值
     * @see Double#isNaN() Double 非数字校验
     * @see Double#isInfinite() Double 无穷大校验
     * @see Float#isNaN() Float 非数字校验
     * @see Float#isInfinite() Float 无穷大校验
     */
    public static void validateLegalFloat(Number number) throws IllegalArgumentException {
        if (!isLegalFloat(number)) {
            throw new IllegalArgumentException(String.format("非法浮点值不支持数值操作：%s（仅支持有限数值）", number));
        }
    }

    /**
     * 校验 Number 是否为合法浮点值（非 NaN/Infinity），仅针对 Double/Float 类型（JDK 1.8+ 兼容）
     * <p>
     * 核心作用：为数值操作（比较、转换、运算、判定）提供前置非法值校验，避免因 NaN/Infinity 导致的不可预期结果（如 NaN.equals(NaN) → false）。
     * <p>
     * 校验逻辑：
     * <ol>
     * <li>空值处理：输入为 null 时返回 true（null 需调用方自行处理，本方法不视为非法）；</li>
     * <li>非浮点类型（int/long/BigDecimal/BigInteger 等）：天然无 NaN/Infinity，返回 true；</li>
     * <li>Double 类型：通过 {@link Double#isNaN()}、{@link Double#isInfinite()} 校验，非 NaN/Infinity 返回 true，否则返回 false；</li>
     * <li>Float 类型：通过 {@link Float#isNaN()}、{@link Float#isInfinite()} 校验，非 NaN/Infinity 返回 true，否则返回 false。</li>
     * </ol>
     *
     * @param number 待校验的 Number 对象（可为 null，null 不视为非法）
     * @return true：数值合法（非 NaN/Infinity，或为非浮点类型/null）；false：数值非法（Double/Float 类型的 NaN 或 Infinity）
     * @see Double#isNaN() Double 非数字校验
     * @see Double#isInfinite() Double 无穷大校验
     * @see Float#isNaN() Float 非数字校验
     * @see Float#isInfinite() Float 无穷大校验
     */
    public static boolean isLegalFloat(Number number) {
        if (number == null) {
            return true;
        }

        if (number instanceof Double) {
            Double doubleVal = (Double) number;
            return !doubleVal.isNaN() && !doubleVal.isInfinite();
        }

        if (number instanceof Float) {
            Float floatVal = (Float) number;
            return !floatVal.isNaN() && !floatVal.isInfinite();
        }

        return true;
    }

    /**
     * 判断数值是否包含「小数点后有效数值」（非零小数部分）
     * <p>
     * 定义：小数部分严格不等于 0（如 10.5→true，10.0→false，10.000→false，-5.1→true）
     * <p>
     * 核心优势：1. 逻辑极简：统一通过 toBigDecimal 转换后判定，避免冗余类型分支；2. 精度无丢失：依赖 toBigDecimal 的字符串转换逻辑，完全规避 Float/Double 二进制精度陷阱；3. 高效快速：整形类型早期直接返回，无需额外计算。
     * <p>
     * 注意事项：
     * <ul>
     * <li>整形类型（Byte/Short/Integer/Long/BigInteger）直接返回 false，无小数部分；</li>
     * <li>Float/Double 依赖其 toString() 进行转换（设计为人类可读格式），与底层二进制存储值可能存在舍入差异，但完全符合数值判定的实际需求；</li>
     * <li>NaN/Infinity 等非法浮点值直接返回 false；</li>
     * <li>自定义 Number 子类需正确实现 toString()（返回标准数值格式），否则转换失败会抛出 NumberFormatException。</li>
     * </ul>
     *
     * @param number 待判断的数值（不可为 null）
     * @return true：存在有效小数部分；false：无有效小数（整数或非法值）
     * @throws IllegalArgumentException 若输入 number 为 null
     * @throws NumberFormatException    若自定义 Number 子类转换 BigDecimal 失败（如 toString() 返回非数值格式）
     */
    public static boolean isDecimalHasEffectiveFraction(@NonNull Number number) {
        if (!isLegalFloat(number)) {
            return false;
        }
        if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long
                || number instanceof BigInteger) {
            return false;
        }
        return !isZero(toBigDecimal(number).remainder(BigDecimal.ONE));
    }

    /**
     * Number 转 BigInteger（精确转换，无精度丢失）
     * <p>
     * 核心逻辑：优先处理直接类型和高效转换，兜底通过 BigDecimal 精确转换，确保无小数截断、无精度损失。
     * <p>
     * 转换流程（按优先级）：
     * <ol>
     * <li>空值兼容：输入为 null 时返回 null；</li>
     * <li>直接类型转换：BigInteger 实例直接返回，无额外开销；</li>
     * <li>高效整数转换：Long/Integer/Short/Byte/AtomicInteger/AtomicLong 转 BigInteger（复用 JDK 缓存，性能最优）；</li>
     * <li>精准兜底转换：其他 Number 子类先转 BigDecimal（通过 {@link #toBigDecimal(Number)}），再调用 {@link BigDecimal#toBigIntegerExact()} 精确转换（小数、非法值会抛异常）。</li>
     * </ol>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 BigInteger；输入为 null 时返回 null
     * @throws ArithmeticException      若数值包含有效小数部分（如 10.5）、超出 BigInteger 表示范围（理论上无，仅极端场景）
     * @throws IllegalArgumentException 若输入为 Double/Float 类型的 NaN 或 Infinity
     * @throws NumberFormatException    若自定义 Number 子类的 toString() 非标准数值格式
     * @see #toBigDecimal(Number) 无精度丢失的 BigDecimal 转换（兜底依赖）
     * @see BigDecimal#toBigIntegerExact() 精确转 BigInteger（禁止小数截断）
     */
    public static BigInteger toBigInteger(Number number) {
        if (number == null) {
            return null;
        }

        if (number instanceof BigInteger) {
            return (BigInteger) number;
        }

        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte
                || number instanceof AtomicInteger || number instanceof AtomicLong) {
            return BigInteger.valueOf(number.longValue());
        }

        return toBigDecimal(number).toBigIntegerExact();
    }

    /**
     * Number 转 Long（精确转换，无精度丢失）
     * <p>
     * 核心逻辑：优先直接转换兼容类型，兜底通过 BigInteger 精确转换，确保无精度丢失、无溢出。
     * <p>
     * 注意事项：
     * <ul>
     * <li>兼容类型（Long/Integer/Short/Byte/AtomicLong/AtomicInteger）直接转换，无精度丢失；</li>
     * <li>其他类型通过 BigInteger 中转，若数值超出 Long 范围（-9223372036854775808 至 9223372036854775807），会抛出溢出异常；</li>
     * <li>若数值包含有效小数部分，会抛出精度损失异常。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 Long；输入为 null 时返回 null
     * @throws ArithmeticException 若数值超出 Long 范围、包含有效小数部分（精度丢失）
     */
    public static Long toLong(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte
                || number instanceof AtomicLong || number instanceof AtomicInteger) {
            return number.longValue();
        }

        return toBigInteger(number).longValueExact();
    }

    /**
     * Number 转 Integer（精确转换，无精度丢失）
     * <p>
     * 核心逻辑：优先直接转换兼容类型，兜底通过 Long 精确转换，确保无精度丢失、无溢出。
     * <p>
     * 注意事项：
     * <ul>
     * <li>兼容类型（Integer/Short/Byte/AtomicInteger）直接转换，无精度丢失；</li>
     * <li>其他类型通过 Long 中转，若数值超出 Integer 范围（-2147483648 至 2147483647），会抛出溢出异常；</li>
     * <li>若数值包含有效小数部分，会抛出精度损失异常。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 Integer；输入为 null 时返回 null
     * @throws ArithmeticException 若数值超出 Integer 范围、包含有效小数部分（精度丢失）
     */
    public static Integer toInteger(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        if (number instanceof Integer || number instanceof Short || number instanceof Byte
                || number instanceof AtomicInteger) {
            return number.intValue();
        }

        return Math.toIntExact(toLong(number));
    }

    /**
     * Number 转 Double（无隐性精度丢失，超出范围/精度自动抛异常）
     * <p>
     * 核心逻辑：通过「范围校验+有效数字校验+反向兜底校验」三层防护，确保转换无溢出、无精度丢失。
     * <p>
     * 精度限制说明：
     * <ul>
     * <li>数值需在 Double 可表示范围（-1.7977E308 至 1.7977E308）内，超出则抛出溢出异常；</li>
     * <li>有效数字位数需 ≤17 位（Double 最大有效数字位数），超则抛出精度丢失异常；</li>
     * <li>整数类型 ≤2^53（9007199254740992）时可精确转换，超出则转入统一校验。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 Double；输入为 null 时返回 null
     * @throws ArithmeticException      若数值超出 Double 范围、有效数字超界、无法精确表示
     * @throws IllegalArgumentException 若输入为 Double/Float 类型的 NaN 或 Infinity（由 {@link #toBigDecimal(Number)} 间接触发）
     */
    public static Double toDouble(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        if (number instanceof Double) {
            return (Double) number;
        }
        if (number instanceof Float) {
            return ((Float) number).doubleValue();
        }

        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte
                || number instanceof AtomicInteger || number instanceof AtomicLong) {
            long longVal = number.longValue();
            if (longVal >= -DOUBLE_EXACT_LONG_THRESHOLD && longVal <= DOUBLE_EXACT_LONG_THRESHOLD) {
                return (double) longVal;
            }
        }

        BigDecimal bigDecimal = toBigDecimal(number);
        BigDecimal doubleMax = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal doubleMinNeg = BigDecimal.valueOf(-Double.MAX_VALUE);

        if (bigDecimal.compareTo(doubleMax) > 0) {
            throw new ArithmeticException(String.format("数值[%s]超出 Double 最大范围[%s]，转换会导致溢出", bigDecimal, doubleMax));
        }
        if (bigDecimal.compareTo(doubleMinNeg) < 0) {
            throw new ArithmeticException(String.format("数值[%s]超出 Double 最小范围[%s]，转换会导致溢出", bigDecimal, doubleMinNeg));
        }

        int effectiveDigits = bigDecimal.precision();
        if (effectiveDigits > DOUBLE_MAX_EFFECTIVE_DIGITS) {
            throw new ArithmeticException(String.format("数值[%s]有效数字位数[%d]超出 Double 上限[%d]，转换会导致小数精度丢失", bigDecimal,
                    effectiveDigits, DOUBLE_MAX_EFFECTIVE_DIGITS));
        }

        Double result = bigDecimal.doubleValue();
        BigDecimal reversedBigDecimal = new BigDecimal(result.toString());
        if (!equals(bigDecimal, reversedBigDecimal)) {
            throw new ArithmeticException(String.format("数值[%s]无法被 Double 精确表示，转换会导致隐性精度丢失", bigDecimal));
        }

        return result;
    }

    /**
     * Number 转 Float（无隐性精度丢失，超出范围/精度自动抛异常）
     * <p>
     * 核心逻辑：通过「范围校验+有效数字校验+反向兜底校验」三层防护，确保转换无溢出、无精度丢失。
     * <p>
     * 精度限制说明：
     * <ul>
     * <li>数值需在 Float 可表示范围（-3.4028E38 至 3.4028E38）内，超出则抛出溢出异常；</li>
     * <li>有效数字位数需 ≤7 位（Float 最大有效数字位数），超则抛出精度丢失异常；</li>
     * <li>整数类型 ≤2^24（16777216）时可精确转换，超出则转入统一校验。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 Float；输入为 null 时返回 null
     * @throws ArithmeticException      若数值超出 Float 范围、有效数字超界、无法精确表示
     * @throws IllegalArgumentException 若输入为 Double/Float 类型的 NaN 或 Infinity（由 {@link #toBigDecimal(Number)} 间接触发）
     */
    public static Float toFloat(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        if (number instanceof Float) {
            return (Float) number;
        }

        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte
                || number instanceof AtomicInteger || number instanceof AtomicLong) {
            long longVal = number.longValue();
            if (longVal >= -FLOAT_EXACT_INT_THRESHOLD && longVal <= FLOAT_EXACT_INT_THRESHOLD) {
                return (float) longVal;
            }
        }

        BigDecimal bigDecimal = toBigDecimal(number);
        BigDecimal floatMax = BigDecimal.valueOf(Float.MAX_VALUE);
        BigDecimal floatMinNeg = BigDecimal.valueOf(-Float.MAX_VALUE);

        if (bigDecimal.compareTo(floatMax) > 0) {
            throw new ArithmeticException(String.format("数值[%s]超出 Float 最大范围[%s]，转换会导致溢出", bigDecimal, floatMax));
        }
        if (bigDecimal.compareTo(floatMinNeg) < 0) {
            throw new ArithmeticException(String.format("数值[%s]超出 Float 最小范围[%s]，转换会导致溢出", bigDecimal, floatMinNeg));
        }

        int effectiveDigits = bigDecimal.precision();
        if (effectiveDigits > FLOAT_MAX_EFFECTIVE_DIGITS) {
            throw new ArithmeticException(String.format("数值[%s]有效数字位数[%d]超出 Float 上限[%d]，转换会导致小数精度丢失", bigDecimal,
                    effectiveDigits, FLOAT_MAX_EFFECTIVE_DIGITS));
        }

        Float result = bigDecimal.floatValue();
        BigDecimal reversedBigDecimal = new BigDecimal(result.toString());
        if (!equals(bigDecimal, reversedBigDecimal)) {
            throw new ArithmeticException(String.format("数值[%s]无法被 Float 精确表示，转换会导致隐性精度丢失", bigDecimal));
        }

        return result;
    }

    /**
     * Number 转 Short（精确转换，无精度丢失）
     * <p>
     * 核心逻辑：优先直接转换兼容类型，兜底通过 BigInteger 精确转换，确保无精度丢失、无溢出。
     * <p>
     * 注意事项：
     * <ul>
     * <li>兼容类型（Short/Byte）直接转换，无精度丢失；</li>
     * <li>其他类型通过 BigInteger 中转，若数值超出 Short 范围（-32768 至 32767），会抛出溢出异常；</li>
     * <li>若数值包含有效小数部分，会抛出精度损失异常。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 Short；输入为 null 时返回 null
     * @throws ArithmeticException 若数值超出 Short 范围、包含有效小数部分（精度丢失）
     */
    public static Short toShort(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        if (number instanceof Short || number instanceof Byte) {
            return number.shortValue();
        }

        return toBigInteger(number).shortValueExact();
    }

    /**
     * Number 转 Byte（精确转换，无精度丢失）
     * <p>
     * 核心逻辑：优先直接转换兼容类型，兜底通过 BigInteger 精确转换，确保无精度丢失、无溢出。
     * <p>
     * 注意事项：
     * <ul>
     * <li>兼容类型（Byte）直接转换，无精度丢失；</li>
     * <li>其他类型通过 BigInteger 中转，若数值超出 Byte 范围（-128 至 127），会抛出溢出异常；</li>
     * <li>若数值包含有效小数部分，会抛出精度损失异常。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return 转换后的 Byte；输入为 null 时返回 null
     * @throws ArithmeticException 若数值超出 Byte 范围、包含有效小数部分（精度丢失）
     */
    public static Byte toByte(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        if (number instanceof Byte) {
            return (Byte) number;
        }

        return toBigInteger(number).byteValueExact();
    }

    /**
     * Number 转 Character（精确转换，基于 Unicode 编码）
     * <p>
     * 核心逻辑：先将 Number 转为 Integer，再基于 Unicode 编码转为 Character，确保编码有效性。
     * <p>
     * 注意事项：
     * <ul>
     * <li>数值需在 Character 编码范围（0 至 65535）内，超出范围会抛出异常；</li>
     * <li>若数值包含有效小数部分，会抛出精度损失异常；</li>
     * <li>转换结果对应 Unicode 编码字符（如数值 65 对应 'A'）。</li>
     * </ul>
     *
     * @param number 待转换的 Number 实例（不可为 null）
     * @return 转换后的 Character；输入为 null 时返回 null
     * @throws ArithmeticException 若数值超出 Character 编码范围、包含有效小数部分（精度丢失）
     */
    public static Character toCharacter(Number number) throws ArithmeticException {
        if (number == null) {
            return null;
        }

        int charValue = toInteger(number);
        if (charValue > Character.MAX_VALUE) {
            throw new ArithmeticException(
                    String.format("数值[%s]大于 Character 最大编码值 %d，无法转换", number, Character.MAX_VALUE));
        }

        if (charValue < 0) {
            throw new ArithmeticException(String.format("数值[%s]小于 Character 最小编码值 0，无法转换", number));
        }
        return (char) charValue;
    }

    /**
     * Number 转 Boolean（基于数值是否为 0 判定）
     * <p>
     * 核心逻辑：数值为 0 则返回 false，非 0 则返回 true，支持所有 Number 子类。
     * <p>
     * 特性：自动处理 null 值（返回 null）、非法浮点值（抛出异常），判定逻辑与 {@link #isZero(Number)} 保持一致。
     *
     * @param number 待转换的 Number 实例（可为 null）
     * @return false=数值为 0；true=数值非 0；null=输入为 null
     * @throws IllegalArgumentException 若数值为 Double/Float 类型的 NaN 或 Infinity
     * @see #isZero(Number) 零值判定核心依赖
     */
    public static Boolean toBoolean(Number number) {
        if (number == null) {
            return null;
        }

        return isZero(number) ? false : true;
    }
}