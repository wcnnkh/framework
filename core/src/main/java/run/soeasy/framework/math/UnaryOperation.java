package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.UnaryOperator;

/**
 * 一元运算标准接口，定义多类型精准运算的统一契约，提供默认类型分发逻辑。
 * <p>
 * 核心设计目标（对齐二元运算接口）：
 * 1. 支持原生基础类型（byte/short/int/long/float/double）和高精度类型（BigInteger/BigDecimal）的精准运算；
 * 2. 提供默认的类型分发逻辑，自动适配任意 Number 类型输入，无需实现类关注类型转换；
 * 3. 遵循「最小兼容类型」原则分发运算，兼顾执行效率与数据兼容性；
 * 4. 兼容自定义 Number 子类，通过兜底逻辑保证运算精准性。
 * <p>
 * 实现类规范：
 * - 必须实现所有 {@code eval} 重载方法，专注于对应类型的核心运算逻辑（如绝对值、取反、自增等）；
 * - 原生类型运算需保证高效性，浮点类型建议支持精度检测（如需）；
 * - 高精度类型运算需保证无精度丢失或溢出（依赖 BigInteger/BigDecimal 原生能力）。
 *
 * @author soeasy.run
 * @see UnaryOperator
 * @see Number
 */
public interface UnaryOperation extends UnaryOperator<Number> {

    // ====================== 1. 运算核心方法（实现类必须实现） ======================

    /**
     * 高精度小数（BigDecimal）一元运算，保证绝对精准无误差。
     * <p>
     * 适用于货币计算、科学计算等精度敏感场景（如 BigDecimal 绝对值、取反）。
     *
     * @param operand 操作数（不可为 null，建议通过 {@link BigDecimal#valueOf(double)} 或字符串构造保证精准）
     * @return 运算结果（通常为 BigDecimal 类型，保证精度不丢失）
     * @see BigDecimal#abs()
     * @see BigDecimal#negate()
     */
    Number eval(BigDecimal operand);

    /**
     * 高精度整数（BigInteger）一元运算，支持超大整数无溢出运算。
     * <p>
     * 适用于超出 long 范围的整数运算（如 BigInteger 绝对值、取反）。
     *
     * @param operand 操作数（不可为 null）
     * @return 运算结果（通常为 BigInteger 类型，无溢出风险）
     * @see BigInteger#abs()
     * @see BigInteger#negate()
     */
    Number eval(BigInteger operand);

    /**
     * 双精度浮点（double）一元运算，兼顾效率与常规精度需求。
     * <p>
     * 适用于常规浮点运算（如 double 绝对值、取反），注意 double 类型的二进制存储精度限制。
     *
     * @param operand 操作数（原生 double 类型）
     * @return 运算结果（可为 double 或 BigDecimal，根据精度需求决定）
     */
    Number eval(double operand);

    /**
     * 长整数（long）一元运算，适用于较大整数且无溢出风险的场景。
     * <p>
     * 如 long 绝对值、自增等，若运算结果可能超出 long 范围，需升级为 BigInteger。
     *
     * @param operand 操作数（原生 long 类型）
     * @return 运算结果（通常为 long 类型，溢出时建议返回 BigInteger）
     */
    Number eval(long operand);

    /**
     * 单精度浮点（float）一元运算，适用于内存敏感、精度要求较低的场景。
     * <p>
     * 注意：float 精度较低（6-7 位有效数字），建议优先转为 double 运算或升级为 BigDecimal。
     *
     * @param operand 操作数（原生 float 类型）
     * @return 运算结果（可为 float、double 或 BigDecimal）
     */
    Number eval(float operand);

    /**
     * 整数（int）一元运算，最常用的基础运算场景（如 int 绝对值、自增）。
     * <p>
     * 若运算结果可能超出 int 范围，需升级为 long 或 BigInteger 避免溢出。
     *
     * @param operand 操作数（原生 int 类型）
     * @return 运算结果（通常为 int 类型，溢出时建议返回 long）
     */
    Number eval(int operand);

    /**
     * 短整数（short）一元运算，适用于内存占用敏感的小范围整数场景。
     * <p>
     * short 范围较小（-32768 至 32767），运算易溢出，需升级为 int 运算后按需转回。
     *
     * @param operand 操作数（原生 short 类型）
     * @return 运算结果（通常为 short 类型，溢出时建议返回 int）
     */
    Number eval(short operand);

    /**
     * 字节（byte）一元运算，适用于二进制数据、极小范围整数场景。
     * <p>
     * byte 范围极小（-128 至 127），运算极易溢出，需升级为 int 运算后按需转回。
     *
     * @param operand 操作数（原生 byte 类型）
     * @return 运算结果（通常为 byte 类型，溢出时建议返回 int）
     */
    Number eval(byte operand);

    // ====================== 2. 默认实现：纯 Class.isInstance 类型分发 ======================

    /**
     * 统一入口方法：接收任意 Number 类型操作数，自动分发至对应类型的 {@code eval} 方法。
     * <p>
     * 分发逻辑优先级（从高到低）：
     * 1. 空指针校验 → 抛出非法参数异常；
     * 2. 高精度类型匹配 → 操作数为 BigDecimal/BigInteger 时触发；
     * 3. 原生类型适配 → 按「最小兼容类型」顺序分发（double → float → long → int → short → byte）；
     * 4. 兜底处理 → 兼容自定义 Number 子类，转为 BigDecimal 运算保证精准。
     * <p>
     * 类型分发原则：
     * - 优先保证精度不丢失（高精度类型不降级）；
     * - 其次保证效率（原生类型优先于高精度类型）；
     * - 最后保证兼容性（自定义类型自动适配高精度）。
     *
     * @param operand 操作数（支持所有 Number 子类，不可为 null）
     * @return 运算结果（类型与运算类型匹配，如 int 绝对值返回 Integer，BigDecimal 取反返回 BigDecimal）
     * @throws IllegalArgumentException 当 operand 为 null 时抛出
     * @see #eval(BigDecimal)
     * @see #eval(double)
     */
    @Override
    default Number apply(Number operand) {
        // 1. 空指针校验（通用容错）
        if (operand == null) {
            throw new IllegalArgumentException("运算参数不能为 null");
        }

        // 2. 优先匹配高精度类型（保证精度不丢失）
        if (BigDecimal.class.isInstance(operand)) {
            return eval((BigDecimal) operand);
        }
        if (BigInteger.class.isInstance(operand)) {
            return eval((BigInteger) operand);
        }

        // 3. 按「最小兼容类型」分发（兼顾效率和兼容性）
        // 顺序说明：double 兼容所有浮点+整数 → float → long → int → short → byte
        if (Double.class.isInstance(operand)) {
            return eval(operand.doubleValue());
        }
        if (Float.class.isInstance(operand)) {
            return eval(operand.floatValue());
        }
        if (Long.class.isInstance(operand)) {
            return eval(operand.longValue());
        }
        if (Integer.class.isInstance(operand)) {
            return eval(operand.intValue());
        }
        if (Short.class.isInstance(operand)) {
            return eval(operand.shortValue());
        }
        if (Byte.class.isInstance(operand)) {
            return eval(operand.byteValue());
        }

        // 4. 兜底：兼容自定义 Number 子类，用 BigDecimal 保证精准
        return eval(new BigDecimal(operand.toString()));
    }
}