package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 减法运算实现类：遵循 {@link BinaryOperation} 接口规范，支持所有数值类型的精准减法（left - right）。
 * <p>
 * 核心逻辑：
 * - 高精度类型：调用原生 subtract 方法，精准无误差；
 * - 原生整数类型：处理溢出（如 int→long、long→BigInteger），避免数据错误；
 * - 原生浮点类型：转 BigDecimal 校准精度，平衡效率与精准度；
 * - 边界场景：Long.MIN_VALUE - 1、Short.MIN_VALUE - (-1) 等溢出自动升级。
 *
 * @author soeasy.run
 * @see BinaryOperation 二元运算标准接口
 */
public class SubtractOperation implements BinaryOperation {

    private static final double FLOAT_PRECISION_THRESHOLD = 1e-10;

    // ====================== 高精度类型减法 ======================
    @Override
    public Number eval(BigDecimal left, BigDecimal right) {
        return left.subtract(right);
    }

    @Override
    public Number eval(BigInteger left, BigInteger right) {
        return left.subtract(right);
    }

    // ====================== 原生浮点类型减法 ======================
    @Override
    public Number eval(double left, double right) {
        double nativeResult = left - right;
        BigDecimal preciseResult = new BigDecimal(Double.toString(left))
                .subtract(new BigDecimal(Double.toString(right)));

        return Math.abs(nativeResult - preciseResult.doubleValue()) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    @Override
    public Number eval(float left, float right) {
        float nativeResult = left - right;
        BigDecimal preciseResult = new BigDecimal(Float.toString(left))
                .subtract(new BigDecimal(Float.toString(right)));

        return Math.abs(nativeResult - preciseResult.floatValue()) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    // ====================== 原生整数类型减法（处理溢出） ======================
    @Override
    public Number eval(long left, long right) {
        try {
            return Math.subtractExact(left, right);
        } catch (ArithmeticException e) {
            return BigInteger.valueOf(left).subtract(BigInteger.valueOf(right));
        }
    }

    @Override
    public Number eval(int left, int right) {
        try {
            return Math.subtractExact(left, right);
        } catch (ArithmeticException e) {
            return (long) left - right;
        }
    }

    @Override
    public Number eval(short left, short right) {
        int intResult = left - right;
        return (intResult >= Short.MIN_VALUE && intResult <= Short.MAX_VALUE)
                ? (short) intResult
                : intResult;
    }

    @Override
    public Number eval(byte left, byte right) {
        int intResult = left - right;
        return (intResult >= Byte.MIN_VALUE && intResult <= Byte.MAX_VALUE)
                ? (byte) intResult
                : intResult;
    }
}