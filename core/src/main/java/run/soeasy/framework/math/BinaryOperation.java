package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BinaryOperator;

import lombok.NonNull;

/**
 * 二元运算标准接口，定义多类型精准运算的统一契约，提供默认类型分发逻辑。
 * <p>
 * 核心设计目标： 1.
 * 支持原生基础类型（byte/short/int/long/float/double）和高精度类型（BigInteger/BigDecimal）的精准运算；
 * 2. 提供默认的类型分发逻辑，自动适配混合类型运算（如 int+double），无需实现类关注类型转换； 3.
 * 遵循「最小兼容类型」原则分发运算，兼顾执行效率与数据兼容性； 4. 兼容自定义 Number 子类，通过兜底逻辑保证运算精准性。
 * <p>
 * 实现类规范： - 必须实现所有 {@code eval} 重载方法，专注于对应类型的核心运算逻辑； -
 * 原生类型运算需保证高效性，浮点类型（float/double）建议支持精度检测与自动升级（如转 BigDecimal）； -
 * 高精度类型运算需保证无精度丢失或溢出（依赖 BigInteger/BigDecimal 原生能力）。
 *
 * @author soeasy.run
 * @see BinaryOperator
 * @see Number
 */
public interface BinaryOperation extends BinaryOperator<Number> {

	// ====================== 1. 运算核心方法（实现类必须实现） ======================

	/**
	 * 高精度小数（BigDecimal）二元运算，保证绝对精准无误差。
	 * <p>
	 * 适用于货币计算、科学计算等精度敏感场景，运算逻辑直接依赖 BigDecimal 原生方法。
	 *
	 * @param left  左操作数（不可为 null，建议通过 {@link BigDecimal#valueOf(double)}
	 *              或字符串构造保证精准）
	 * @param right 右操作数（不可为 null，要求同左操作数）
	 * @return 运算结果（通常为 BigDecimal 类型，保证精度不丢失）
	 * @see BigDecimal#add(BigDecimal)
	 * @see BigDecimal#subtract(BigDecimal)
	 */
	Number eval(BigDecimal left, BigDecimal right);

	/**
	 * 高精度整数（BigInteger）二元运算，支持超大整数无溢出运算。
	 * <p>
	 * 适用于超出 long 范围的整数运算，避免原生类型溢出问题。
	 *
	 * @param left  左操作数（不可为 null）
	 * @param right 右操作数（不可为 null）
	 * @return 运算结果（通常为 BigInteger 类型，无溢出风险）
	 * @see BigInteger#add(BigInteger)
	 * @see BigInteger#multiply(BigInteger)
	 */
	Number eval(BigInteger left, BigInteger right);

	/**
	 * 双精度浮点（double）二元运算，兼顾效率与常规精度需求。
	 * <p>
	 * 注意：double 类型存在二进制存储精度限制（如 0.1+0.2 可能存在误差）， 实现类建议添加精度检测逻辑，必要时自动升级为 BigDecimal
	 * 运算。
	 *
	 * @param left  左操作数（原生 double 类型）
	 * @param right 右操作数（原生 double 类型）
	 * @return 运算结果（可为 double 或 BigDecimal，根据精度需求决定）
	 */
	Number eval(double left, double right);

	/**
	 * 长整数（long）二元运算，适用于较大整数且无溢出风险的场景。
	 * <p>
	 * 若运算结果可能超出 long 范围（-9223372036854775808 至 9223372036854775807）， 实现类需手动升级为
	 * BigInteger 运算避免溢出。
	 *
	 * @param left  左操作数（原生 long 类型）
	 * @param right 右操作数（原生 long 类型）
	 * @return 运算结果（通常为 long 类型，溢出时建议返回 BigInteger）
	 */
	Number eval(long left, long right);

	/**
	 * 单精度浮点（float）二元运算，适用于内存敏感、精度要求较低的场景。
	 * <p>
	 * 注意：float 精度低于 double（仅 6-7 位有效数字），易产生精度丢失， 实现类建议优先转为 double 运算或直接升级为
	 * BigDecimal。
	 *
	 * @param left  左操作数（原生 float 类型）
	 * @param right 右操作数（原生 float 类型）
	 * @return 运算结果（可为 float、double 或 BigDecimal）
	 */
	Number eval(float left, float right);

	/**
	 * 整数（int）二元运算，最常用的基础运算场景，高效无冗余。
	 * <p>
	 * 若运算结果可能超出 int 范围（-2147483648 至 2147483647）， 实现类需手动升级为 long 或 BigInteger
	 * 运算避免溢出。
	 *
	 * @param left  左操作数（原生 int 类型）
	 * @param right 右操作数（原生 int 类型）
	 * @return 运算结果（通常为 int 类型，溢出时建议返回 long）
	 */
	Number eval(int left, int right);

	/**
	 * 短整数（short）二元运算，适用于内存占用敏感的小范围整数场景。
	 * <p>
	 * 注意：short 范围较小（-32768 至 32767），运算易溢出， 实现类需手动升级为 int 运算后按需转回 short（确保无溢出时）。
	 *
	 * @param left  左操作数（原生 short 类型）
	 * @param right 右操作数（原生 short 类型）
	 * @return 运算结果（通常为 short 类型，溢出时建议返回 int）
	 */
	Number eval(short left, short right);

	/**
	 * 字节（byte）二元运算，适用于二进制数据、极小范围整数场景。
	 * <p>
	 * 注意：byte 范围极小（-128 至 127），运算极易溢出， 实现类需手动升级为 int 运算后按需转回 byte（确保无溢出时）。
	 *
	 * @param left  左操作数（原生 byte 类型）
	 * @param right 右操作数（原生 byte 类型）
	 * @return 运算结果（通常为 byte 类型，溢出时建议返回 int）
	 */
	Number eval(byte left, byte right);

	// ====================== 2. 默认实现：纯 Class.isInstance 类型分发 ======================

	/**
	 * 统一入口方法：接收任意 Number 类型操作数，自动分发至对应类型的 {@code eval} 方法。
	 * <p>
	 * 分发逻辑优先级（从高到低）： 1. 空指针校验 → 抛出非法参数异常； 2. 高精度类型匹配 → 仅当两个操作数均为
	 * BigDecimal/BigInteger 时触发； 3. 混合类型适配 → 按「最小兼容类型」顺序分发（double → float → long →
	 * int → short → byte）； 4. 兜底处理 → 兼容自定义 Number 子类，转为 BigDecimal 运算保证精准。
	 * <p>
	 * 类型分发原则： - 优先保证精度不丢失（高精度类型不降级）； - 其次保证效率（原生类型优先于高精度类型）； -
	 * 最后保证兼容性（混合类型自动向上适配最小兼容类型）。
	 *
	 * @param left  左操作数（支持所有 Number 子类，不可为 null）
	 * @param right 右操作数（支持所有 Number 子类，不可为 null）
	 * @return 运算结果（类型与运算类型匹配，如 int+int 返回 Integer，0.1+0.2 可能返回 BigDecimal）
	 * @throws IllegalArgumentException 当 left 或 right 为 null 时抛出
	 * @see #eval(BigDecimal, BigDecimal)
	 * @see #eval(double, double)
	 */
	@Override
	default Number apply(@NonNull Number left, @NonNull Number right) {
		// 2. 优先匹配高精度类型（避免被低精度类型覆盖，保证精度不丢失）
		if (BigDecimal.class.isInstance(left) && BigDecimal.class.isInstance(right)) {
			return eval((BigDecimal) left, (BigDecimal) right);
		}
		if (BigInteger.class.isInstance(left) && BigInteger.class.isInstance(right)) {
			return eval((BigInteger) left, (BigInteger) right);
		}

		// 3. 按「最小兼容类型」分发（兼顾效率和兼容性，自动向上适配）
		// 顺序说明：double 兼容所有浮点+整数 → float 兼容浮点+整数 → long 兼容所有整数 → 依次向下
		if (Double.class.isInstance(left) || Double.class.isInstance(right)) {
			return eval(left.doubleValue(), right.doubleValue());
		}
		if (Float.class.isInstance(left) || Float.class.isInstance(right)) {
			return eval(left.floatValue(), right.floatValue());
		}
		if (Long.class.isInstance(left) || Long.class.isInstance(right)) {
			return eval(left.longValue(), right.longValue());
		}
		if (Integer.class.isInstance(left) || Integer.class.isInstance(right)) {
			return eval(left.intValue(), right.intValue());
		}
		if (Short.class.isInstance(left) || Short.class.isInstance(right)) {
			return eval(left.shortValue(), right.shortValue());
		}
		if (Byte.class.isInstance(left) && Byte.class.isInstance(right)) {
			return eval(left.byteValue(), right.byteValue());
		}

		// 4. 兜底：兼容自定义 Number 子类（如用户扩展的数值类型），用 BigDecimal 保证精准
		return eval(new BigDecimal(left.toString()), new BigDecimal(right.toString()));
	}
}