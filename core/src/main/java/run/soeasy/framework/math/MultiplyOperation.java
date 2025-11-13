package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 乘法运算实现类：遵循 {@link BinaryOperation} 接口规范，支持所有数值类型的精准乘法。
 * <p>
 * 核心逻辑：
 * - 高精度类型：调用原生 multiply 方法，无精度丢失/溢出；
 * - 原生整数类型：重点处理溢出（乘法溢出概率高于加减），int 溢出转 long，long 溢出转 BigInteger；
 * - 原生浮点类型：转 BigDecimal 运算校准，避免 0.1*3=0.300000004 这类精度误差；
 * - 特殊场景：0 乘以任何数返回 0（保持原类型）、1 乘以任何数返回原数。
 *
 * @author soeasy.run
 * @see BinaryOperation 二元运算标准接口
 */
public class MultiplyOperation implements BinaryOperation {

    private static final double FLOAT_PRECISION_THRESHOLD = 1e-10;

    // ====================== 高精度类型乘法 ======================
    @Override
    public Number eval(BigDecimal left, BigDecimal right) {
        return left.multiply(right);
    }

    @Override
    public Number eval(BigInteger left, BigInteger right) {
        return left.multiply(right);
    }

    // ====================== 原生浮点类型乘法 ======================
    @Override
    public Number eval(double left, double right) {
        // 特殊场景：0 或 1 直接返回结果，避免多余运算
        if (left == 0.0 || right == 0.0) return 0.0;
        if (left == 1.0) return right;
        if (right == 1.0) return left;

        double nativeResult = left * right;
        BigDecimal preciseResult = new BigDecimal(Double.toString(left))
                .multiply(new BigDecimal(Double.toString(right)));

        return Math.abs(nativeResult - preciseResult.doubleValue()) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    @Override
    public Number eval(float left, float right) {
        if (left == 0.0f || right == 0.0f) return 0.0f;
        if (left == 1.0f) return right;
        if (right == 1.0f) return left;

        float nativeResult = left * right;
        BigDecimal preciseResult = new BigDecimal(Float.toString(left))
                .multiply(new BigDecimal(Float.toString(right)));

        return Math.abs(nativeResult - preciseResult.floatValue()) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    // ====================== 原生整数类型乘法（重点处理溢出） ======================
    @Override
    public Number eval(long left, long right) {
        // 特殊场景优化
        if (left == 0 || right == 0) return 0L;
        if (left == 1) return right;
        if (right == 1) return left;

        // 校验溢出：Math.multiplyExact 溢出抛异常
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException e) {
            // 溢出转 BigInteger
            return BigInteger.valueOf(left).multiply(BigInteger.valueOf(right));
        }
    }

    @Override
    public Number eval(int left, int right) {
        if (left == 0 || right == 0) return 0;
        if (left == 1) return right;
        if (right == 1) return left;

        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException e) {
            // int 溢出转 long
            return (long) left * right;
        }
    }

    @Override
    public Number eval(short left, short right) {
        if (left == 0 || right == 0) return (short) 0;
        if (left == 1) return right;
        if (right == 1) return left;

        // short 直接乘法极易溢出，先升级为 int 运算
        int intResult = left * right;
        return (intResult >= Short.MIN_VALUE && intResult <= Short.MAX_VALUE)
                ? (short) intResult
                : intResult;
    }

    @Override
    public Number eval(byte left, byte right) {
        if (left == 0 || right == 0) return (byte) 0;
        if (left == 1) return right;
        if (right == 1) return left;

        // byte 升级为 int 运算
        int intResult = left * right;
        return (intResult >= Byte.MIN_VALUE && intResult <= Byte.MAX_VALUE)
                ? (byte) intResult
                : intResult;
    }
}