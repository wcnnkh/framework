package run.soeasy.framework.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 取模运算实现类：遵循 {@link BinaryOperation} 接口规范，支持所有数值类型的精准取模（left % right）。
 * <p>
 * 核心规则（对齐 Java 原生取模逻辑）：
 * 1. 余数符号与被除数一致（例：-7%3=-1、7%-3=1、-7%-3=-1）；
 * 2. 高精度类型：BigDecimal 用 {@link BigDecimal#remainder(BigDecimal)}，BigInteger 用除法余数（保证符号规则）；
 * 3. 原生类型：优先原生运算，溢出时自动升级为更高精度类型（如 short→int、long→BigInteger）；
 * 4. 异常处理：除数为 0 抛出 {@link ArithmeticException}，明确提示操作数上下文；
 * 5. 特殊场景：
 *    - 0 取模任何非零数 → 0（保持原类型）；
 *    - 任何数取模 1 → 0；
 *    - 任何数取模自身 → 0；
 *    - 任何数取模 0 → 抛出异常。
 *
 * @author soeasy.run
 * @see BinaryOperation 二元运算标准接口
 * @see BigDecimal#remainder(BigDecimal) BigDecimal 取模（遵循符号规则）
 */
public class ModOperation implements BinaryOperation {

    /** 浮点精度阈值：误差小于此值视为无精度丢失（用于判断是否返回原生浮点类型） */
    private static final double FLOAT_PRECISION_THRESHOLD = 1e-10;

    // ====================== 高精度类型取模（精准+符号正确） ======================
    @Override
    public Number eval(BigDecimal left, BigDecimal right) {
        // 除数为 0 校验
        if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景优化
        if (left.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO; // 0%非0 → 0
        if (right.compareTo(BigDecimal.ONE) == 0) return BigDecimal.ZERO; // 任何数%1 → 0
        if (left.compareTo(right) == 0) return BigDecimal.ZERO; // 任何数%自身 → 0

        // BigDecimal.remainder 天然遵循「余数符号与被除数一致」规则
        return left.remainder(right);
    }

    @Override
    public Number eval(BigInteger left, BigInteger right) {
        // 除数为 0 校验
        if (right.compareTo(BigInteger.ZERO) == 0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景优化
        if (left.compareTo(BigInteger.ZERO) == 0) return BigInteger.ZERO;
        if (right.compareTo(BigInteger.ONE) == 0) return BigInteger.ZERO;
        if (left.compareTo(right) == 0) return BigInteger.ZERO;

        // BigInteger 取模：用 divideAndRemainder 获取余数（保证符号与被除数一致）
        // 注意：BigInteger.mod() 会返回非负余数，不符合 Java 原生规则，故不用
        return left.divideAndRemainder(right)[1];
    }

    // ====================== 原生浮点类型取模（校准精度+符号正确） ======================
    @Override
    public Number eval(double left, double right) {
        // 除数为 0 校验（原生 double 除数为0会返回 NaN，此处提前抛出明确异常）
        if (right == 0.0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        // 特殊场景优化
        if (left == 0.0) return 0.0;
        if (right == 1.0) return 0.0;
        if (left == right) return 0.0;

        // 转 BigDecimal 运算：校准精度，同时保证符号规则
        BigDecimal preciseLeft = new BigDecimal(Double.toString(left));
        BigDecimal preciseRight = new BigDecimal(Double.toString(right));
        BigDecimal preciseResult = preciseLeft.remainder(preciseRight);
        double nativeResult = preciseResult.doubleValue();

        // 无精度损失则返回 double，否则返回 BigDecimal（避免 0.3%0.1=0.00000000000000004 这类误差）
        return Math.abs(preciseResult.doubleValue() - nativeResult) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    @Override
    public Number eval(float left, float right) {
        if (right == 0.0f) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        if (left == 0.0f) return 0.0f;
        if (right == 1.0f) return 0.0f;
        if (left == right) return 0.0f;

        BigDecimal preciseLeft = new BigDecimal(Float.toString(left));
        BigDecimal preciseRight = new BigDecimal(Float.toString(right));
        BigDecimal preciseResult = preciseLeft.remainder(preciseRight);
        float nativeResult = preciseResult.floatValue();

        return Math.abs(preciseResult.floatValue() - nativeResult) < FLOAT_PRECISION_THRESHOLD
                ? nativeResult
                : preciseResult;
    }

    // ====================== 原生整数类型取模（处理溢出+符号正确） ======================
    @Override
    public Number eval(long left, long right) {
        if (right == 0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        if (left == 0) return 0L;
        if (right == 1) return 0L;
        if (left == right) return 0L;

        // 处理 long 溢出场景（极端情况：Long.MIN_VALUE % (-1) 会溢出）
        try {
            // 原生 long 取模遵循「余数符号与被除数一致」规则
            return left % right;
        } catch (ArithmeticException e) {
            // 溢出时转 BigInteger 运算
        	return eval(BigInteger.valueOf(left), BigInteger.valueOf(right));
        }
    }

    @Override
    public Number eval(int left, int right) {
        if (right == 0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        if (left == 0) return 0;
        if (right == 1) return 0;
        if (left == right) return 0;

        // 处理 int 溢出（如 Integer.MIN_VALUE % (-1) 溢出）
        try {
            return left % right;
        } catch (ArithmeticException e) {
            // 溢出转 long 运算
            return (long) left % right;
        }
    }

    @Override
    public Number eval(short left, short right) {
        if (right == 0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        if (left == 0) return (short) 0;
        if (right == 1) return (short) 0;
        if (left == right) return (short) 0;

        // short 范围小，先升级为 int 运算（避免直接运算溢出）
        int intResult = left % right;
        // 结果在 short 范围内则转回，否则返回 int
        return (intResult >= Short.MIN_VALUE && intResult <= Short.MAX_VALUE)
                ? (short) intResult
                : intResult;
    }

    @Override
    public Number eval(byte left, byte right) {
        if (right == 0) {
            throw new ArithmeticException(String.format("取模运算：除数不能为0 [left=%s, right=%s]", left, right));
        }
        if (left == 0) return (byte) 0;
        if (right == 1) return (byte) 0;
        if (left == right) return (byte) 0;

        // byte 升级为 int 运算，避免溢出
        int intResult = left % right;
        // 结果在 byte 范围内则转回，否则返回 int
        return (intResult >= Byte.MIN_VALUE && intResult <= Byte.MAX_VALUE)
                ? (byte) intResult
                : intResult;
    }
}