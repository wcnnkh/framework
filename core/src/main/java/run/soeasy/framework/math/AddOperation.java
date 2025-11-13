package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 加法运算实现类：遵循 {@link BinaryOperation} 接口规范，支持所有数值类型的精准加法。
 * <p>
 * 核心逻辑：
 * - 高精度类型（BigDecimal/BigInteger）：直接调用原生 add 方法，保证绝对精准；
 * - 原生整数类型（long/int/short/byte）：优先原生运算，溢出时自动升级为更高精度类型（如 int→long、long→BigInteger）；
 * - 原生浮点类型（double/float）：先转 BigDecimal 运算校准精度，无误差时返回原生类型，有误差时返回 BigDecimal；
 * - 边界处理：Short.MIN_VALUE + (-1)、Long.MIN_VALUE + (-1) 等溢出场景自动升级，避免数据错误。
 *
 * @author soeasy.run
 * @see BinaryOperation 二元运算标准接口
 */
public class AddOperation implements BinaryOperation {

    /** 浮点精度阈值：误差小于此值视为无精度丢失（用于判断是否返回原生浮点类型） */
    private static final double FLOAT_PRECISION_THRESHOLD = 1e-10;

    // ====================== 高精度类型加法（无精度丢失/溢出） ======================
    @Override
    public Number eval(BigDecimal left, BigDecimal right) {
        // BigDecimal 加法：精准无误差，直接返回结果
        return left.add(right);
    }

    @Override
    public Number eval(BigInteger left, BigInteger right) {
        // BigInteger 加法：支持超大整数，无溢出风险
        return left.add(right);
    }

    // ====================== 原生浮点类型加法（处理精度问题） ======================
    @Override
    public Number eval(double left, double right) {
        // 原生 double 加法可能有精度误差（如 0.1+0.2），用 BigDecimal 校准
        double nativeResult = left + right;
        BigDecimal preciseResult = new BigDecimal(Double.toString(left))
                .add(new BigDecimal(Double.toString(right)));

        // 误差在阈值内：返回 double；否则返回 BigDecimal 保证精度
        return Math.abs(nativeResult - preciseResult.doubleValue()) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    @Override
    public Number eval(float left, float right) {
        // float 精度较低，先转 BigDecimal 运算，再判断是否能转回 float
        float nativeResult = left + right;
        BigDecimal preciseResult = new BigDecimal(Float.toString(left))
                .add(new BigDecimal(Float.toString(right)));

        return Math.abs(nativeResult - preciseResult.floatValue()) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    // ====================== 原生整数类型加法（处理溢出） ======================
    @Override
    public Number eval(long left, long right) {
        // 校验 long 加法是否溢出：使用 JDK 8+ Math.addExact 抛异常的特性，捕获后转 BigInteger
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException e) {
            // 溢出时：转 BigInteger 运算，避免数据丢失
            return BigInteger.valueOf(left).add(BigInteger.valueOf(right));
        }
    }

    @Override
    public Number eval(int left, int right) {
        // 校验 int 加法溢出，溢出转 long
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException e) {
            return (long) left + right;
        }
    }

    @Override
    public Number eval(short left, short right) {
        // short 范围小（-32768~32767），先升级为 int 运算，再判断是否能转回 short
        int intResult = left + right;
        return (intResult >= Short.MIN_VALUE && intResult <= Short.MAX_VALUE)
                ? (short) intResult
                : intResult;
    }

    @Override
    public Number eval(byte left, byte right) {
        // byte 范围极小（-128~127），升级为 int 运算，再判断是否能转回 byte
        int intResult = left + right;
        return (intResult >= Byte.MIN_VALUE && intResult <= Byte.MAX_VALUE)
                ? (byte) intResult
                : intResult;
    }
}