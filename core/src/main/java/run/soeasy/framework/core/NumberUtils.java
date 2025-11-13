package run.soeasy.framework.core;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.comparator.NumberComparator;

/**
 * 高精度数值处理工具类（不可实例化），专注于 BigDecimal、BigInteger 及通用 Number 类型的核心操作，
 * 提供「尾部无效零清除、BigInteger 深拷贝、Number 转 BigDecimal（无精度丢失）、精准数值比较、精准正负零判定、非法浮点校验」六大核心功能，
 * 彻底规避 Double/Float 原生浮点精度陷阱，确保高精度数值处理的安全性、独立性、格式优化及精准性，
 * 适用于金融计算、复杂数值运算、单位转换等对高精度数据有严格要求的业务场景。
 * 
 * <p>
 * <strong>设计特性</strong>：
 * <ul>
 * <li><strong>不可实例化</strong>：通过 {@link UtilityClass} 注解标记，自动生成私有构造方法，禁止创建实例，所有方法均为静态；</li>
 * <li><strong>安全可靠</strong>：关键方法通过 {@link NonNull} 注解强制非空校验，处理逻辑覆盖「null 值、0 值、NaN、Infinity、自定义 Number 子类」等边界场景；</li>
 * <li><strong>高精度优先</strong>：核心逻辑基于 BigDecimal/BigInteger 实现，所有转换、比较、判定操作均无精度丢失，完全兼容 JDK 1.8+；</li>
 * <li><strong>格式优化</strong>：清除 BigDecimal 尾部无效零，避免科学计数法，优化数值字符串展示（不改变数值本身）；</li>
 * <li><strong>引用独立</strong>：提供 BigInteger 深拷贝能力，避免多线程/反射场景下的引用共享风险；</li>
 * <li><strong>无额外依赖</strong>：仅依赖 JDK 1.8 原生类和 Lombok 基础注解（{@link UtilityClass}、{@link NonNull}），无需第三方工具包支持。</li>
 * </ul>
 *
 * <p>
 * <strong>核心功能映射</strong>：
 * <ul>
 * <li>格式优化：{@link #stripTrailingZeros(BigDecimal)}（清除 BigDecimal 尾部无效零）；</li>
 * <li>引用安全：{@link #newBigInteger(BigInteger)}（BigInteger 深拷贝，独立引用）；</li>
 * <li>无精度转换：{@link #toBigDecimal(Number)}（Number → BigDecimal，规避浮点精度丢失）；</li>
 * <li>精准比较：{@link #equals(Number, Number)}（跨 Number 类型精准比较，支持所有子类）；</li>
 * <li>精准判定：{@link #isZero(Number)}、{@link #isPositive(Number)}、{@link #isNegative(Number)}（正负零精准判定）；</li>
 * <li>非法值校验：{@link #validateLegalFloat(Number)}（拦截 NaN/Infinity 等非法浮点值）。</li>
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

	/**
	 * 清除 BigDecimal 尾部的无效零，优化数值字符串表示形式（不改变数值本身大小）
	 * <p>
	 * 核心应用场景：金融金额展示、数值日志输出、API 响应格式化（如 123.4500 → 123.45，100.000 → 100）。
	 * <p>
	 * 处理逻辑（按优先级）：
	 * <ol>
	 * <li>空值兼容：输入为 null 时直接返回 null，避免空指针异常；</li>
	 * <li>无需处理场景：数值为 0（{@link BigDecimal#signum()} == 0）或无小数部分（{@link BigDecimal#scale()} ≤ 0），直接返回原对象；</li>
	 * <li>尾部零清除：调用 {@link BigDecimal#stripTrailingZeros()} 清除尾部无效零；</li>
	 * <li>科学计数法规避：清除零后若变为整数（scale ≤ 0），通过 {@link BigDecimal#setScale(int)} 转为 0 位小数格式（例：1E2 → 100）。</li>
	 * </ol>
	 * <p>
	 * <strong>注意事项</strong>：
	 * BigDecimal 为不可变对象，此方法会返回处理后的新实例，需接收返回值方可生效，原输入对象不会被修改。
	 *
	 * @param number 待处理的 BigDecimal 实例（可为 null）
	 * @return 清除尾部无效零后的 BigDecimal；输入为 null 时返回 null
	 * @see BigDecimal#stripTrailingZeros() 原生尾部零清除方法
	 * @see BigDecimal#setScale(int) 小数位数设置（用于规避科学计数法）
	 * @example 示例1：stripTrailingZeros(new BigDecimal("123.4500")) → 123.45
	 * @example 示例2：stripTrailingZeros(new BigDecimal("100.000")) → 100（而非 1E2）
	 * @example 示例3：stripTrailingZeros(new BigDecimal("0.000")) → 0.000（数值为0，直接返回原对象）
	 */
	public static BigDecimal stripTrailingZeros(BigDecimal number) {
		if (number == null) {
			return null;
		}
		// 特殊情况：数值为0或无小数部分，无需处理
		if (number.signum() == 0 || number.scale() <= 0) {
			return number;
		}

		// 清除尾部零后判断是否为整数
		BigDecimal stripped = number.stripTrailingZeros();
		if (stripped.scale() <= 0) {
			// 转为整数形式（避免科学计数法）
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
	 * @example 示例：BigInteger original = BigInteger.valueOf(100);
	 *            BigInteger copy = NumberUtils.newBigInteger(original);
	 *            System.out.println(original.equals(copy)); // true（数值一致）
	 *            System.out.println(original == copy); // false（引用独立）
	 */
	public static BigInteger newBigInteger(@NonNull BigInteger bigInteger) {
		return new BigInteger(bigInteger.toByteArray());
	}

	/**
	 * 安全将非空 Number 转为 BigDecimal（无精度丢失），兼容所有 Number 子类（JDK 1.8+ 适配）
	 * <p>
	 * 核心解决问题：规避 Double/Float 原生浮点类型直接转 BigDecimal 的精度丢失问题（如 new BigDecimal(0.1) → 0.1000000000000000055...）。
	 * <p>
	 * 转换优先级（按「效率+精度」排序）：
	 * <ol>
	 * <li>已为 BigDecimal：直接返回原对象（无转换开销，保持引用不变）；</li>
	 * <li>已为 BigInteger：通过 {@link BigDecimal#BigDecimal(BigInteger)} 转换（精准无误差，支持任意大整数）；</li>
	 * <li>Double/Float 类型：通过 {@link String#valueOf(double)} 转为字符串后构造 BigDecimal（基于字符串的精准转换，规避二进制存储误差）；</li>
	 * <li>原生类型包装类（Integer/Long/Short/Byte/Character）：通过 {@link Number#toString()} 转换（字符串为精准数值表示，无精度损失）；</li>
	 * <li>自定义 Number 子类：依赖其 {@link Number#toString()} 方法（需子类正确实现数值的字符串表示，否则抛出异常）。</li>
	 * </ol>
	 *
	 * @param number 待转换的 Number 对象（不可为 null，由 {@link NonNull} 强制校验）
	 * @return 转换后的 BigDecimal（数值与原 Number 完全一致，无精度丢失）
	 * @throws NullPointerException  若 number 为 null（由 Lombok @NonNull 自动抛出）
	 * @throws NumberFormatException  若自定义 Number 子类的 toString() 返回非数值字符串（如 "abc"），或转换过程中格式非法
	 * @see BigDecimal#BigDecimal(String) 字符串构造方法（无精度丢失核心依赖）
	 * @example 示例1：toBigDecimal(0.1d) → 0.1（而非 0.1000000000000000055...）
	 * @example 示例2：toBigDecimal(Integer.valueOf(100)) → 100
	 * @example 示例3：toBigDecimal(new BigInteger("999999999999999999")) → 999999999999999999
	 */
	public static BigDecimal toBigDecimal(@NonNull Number number) {
		// 1. 直接返回 BigDecimal 类型（避免不必要转换）
		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		}

		// 2. BigInteger 转 BigDecimal（精准无误差）
		if (number instanceof BigInteger) {
			return new BigDecimal((BigInteger) number);
		}

		// 3. Double 类型：通过 toString() 转换（解决精度丢失问题）
		if (number instanceof Double) {
			// 注意：避免使用 new BigDecimal((Double) number)，会保留 double 二进制存储的误差
			return new BigDecimal(Double.toString((Double) number));
		}

		// 4. Float 类型：通过 toString() 转换（同理解决精度丢失）
		if (number instanceof Float) {
			return new BigDecimal(Float.toString((Float) number));
		}

		// 5. 原生类型包装类及其他 Number 子类：通过 toString() 转换（确保精准）
		return new BigDecimal(number.toString());
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
	 * @example 示例1：equals(Integer.valueOf(100), Long.valueOf(100)) → true
	 * @example 示例2：equals(BigDecimal.valueOf(0.1), Double.valueOf(0.1)) → true（无精度丢失）
	 * @example 示例3：equals(null, null) → true；equals(null, 1) → false
	 * @example 示例4：equals(Double.NaN, 0) → 抛出 IllegalArgumentException（非法浮点值）
	 */
	public static boolean equals(Number left, Number right) {
		// 空值判断：异或运算简化「一个null、一个非null」的判定
		if (left == null ^ right == null) {
			return false;
		}
		// 双空判定：均为null → 相等
		if (left == null) {
			return true;
		}

		// 非法浮点值校验：避免 NaN/Infinity 导致的不可预期结果
		validateLegalFloat(left);
		validateLegalFloat(right);

		// 精准比较：原生 equals + 跨类型比较器（兼顾效率和兼容性）
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
	 * @example 示例1：isZero(0) → true；isZero(0.0d) → true；isZero(BigDecimal.ZERO) → true
	 * @example 示例2：isZero(1) → false；isZero(-0.1f) → false；isZero(null) → false
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
	 * @example 示例1：isPositive(10) → true；isPositive(0.0001) → true；isPositive(BigDecimal.ONE) → true
	 * @example 示例2：isPositive(0) → false；isPositive(-5) → false；isPositive(null) → false
	 */
	public static boolean isPositive(Number number) {
		if (number == null) {
			return false;
		}
		// 非法浮点值校验：避免非法值导致的比较异常
		validateLegalFloat(number);
		// 精准判定：数值严格大于 0
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
	 * @example 示例1：isNegative(-5) → true；isNegative(-0.001) → true；isNegative(BigInteger.valueOf(-100)) → true
	 * @example 示例2：isNegative(0) → false；isNegative(3) → false；isNegative(null) → false
	 */
	public static boolean isNegative(Number number) {
		if (number == null) {
			return false;
		}
		// 非法浮点值校验：避免非法值导致的比较异常
		validateLegalFloat(number);
		// 精准判定：数值严格小于 0
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
	 * @example 示例1：validateLegalFloat(Double.NaN) → 抛出 IllegalArgumentException（非法浮点值不支持数值操作：NaN）
	 * @example 示例2：validateLegalFloat(Float.POSITIVE_INFINITY) → 抛出 IllegalArgumentException（非法浮点值不支持数值操作：Infinity）
	 * @example 示例3：validateLegalFloat(10.0d) → 无动作（合法浮点值）
	 */
	public static void validateLegalFloat(Number number) throws IllegalArgumentException {
		if (number instanceof Double) {
			Double doubleVal = (Double) number;
			if (doubleVal.isNaN() || doubleVal.isInfinite()) {
				throw new IllegalArgumentException(String.format("非法浮点值不支持数值操作：%s（仅支持有限数值）", doubleVal));
			}
		} else if (number instanceof Float) {
			Float floatVal = (Float) number;
			if (floatVal.isNaN() || floatVal.isInfinite()) {
				throw new IllegalArgumentException(String.format("非法浮点值不支持数值操作：%s（仅支持有限数值）", floatVal));
			}
		}
		// 非浮点类型（int/long/BigDecimal/BigInteger 等）天然无非法值，无需校验
	}
}