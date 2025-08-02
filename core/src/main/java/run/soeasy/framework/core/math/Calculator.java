package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BinaryOperator;

/**
 * 运算器/计算器接口
 * 
 * @author soeasy.run
 */
public interface Calculator extends BinaryOperator<Number> {

    /**
     * 获取运算符符号
     * 
     * @return 运算符符号（如"+"、"-"）
     */
    String getOperator();

    /**
     * 执行两个数值的运算
     * 
     * @param left 左操作数
     * @param right 右操作数
     * @return 运算结果，类型根据输入类型自动推导
     */
    @Override
    default Number apply(Number left, Number right) {
        // 处理null操作数
        if (left == null) return right;
        if (right == null) return left;
        
        // 快速路径：优先处理基本数据类型（避免BigDecimal开销）
        if (left instanceof Byte && right instanceof Byte) {
            return apply(left.byteValue(), right.byteValue());
        }
        if (left instanceof Short && right instanceof Short) {
            return apply(left.shortValue(), right.shortValue());
        }
        if (left instanceof Integer && right instanceof Integer) {
            return apply(left.intValue(), right.intValue());
        }
        if (left instanceof Long && right instanceof Long) {
            return apply(left.longValue(), right.longValue());
        }
        if (left instanceof Float && right instanceof Float) {
            return apply(left.floatValue(), right.floatValue());
        }
        if (left instanceof Double && right instanceof Double) {
            return apply(left.doubleValue(), right.doubleValue());
        }
        
        // 高精度路径：处理BigInteger/BigDecimal及其他Number子类
        if (left instanceof BigDecimal || right instanceof BigDecimal) {
            return apply(toBigDecimal(left), toBigDecimal(right));
        } else if (left instanceof BigInteger || right instanceof BigInteger) {
            return apply(toBigInteger(left), toBigInteger(right));
        }
        
        // 未知类型降级处理
        return apply(toBigDecimal(left), toBigDecimal(right));
    }

    // ===== 基本数据类型运算方法 =====
    
    /**
     * 执行两个byte类型数值的运算
     * 
     * @param left 左操作数（byte类型）
     * @param right 右操作数（byte类型）
     * @return 运算结果（byte类型）
     */
    default byte apply(byte left, byte right) {
        return (byte) apply((int) left, (int) right);
    }

    /**
     * 执行两个short类型数值的运算
     * 
     * @param left 左操作数（short类型）
     * @param right 右操作数（short类型）
     * @return 运算结果（short类型）
     */
    default short apply(short left, short right) {
        return (short) apply((int) left, (int) right);
    }

    /**
     * 执行两个int类型数值的运算
     * 
     * @param left 左操作数（int类型）
     * @param right 右操作数（int类型）
     * @return 运算结果（int类型）
     */
    default int apply(int left, int right) {
        return (int) apply((long) left, (long) right);
    }

    /**
     * 执行两个long类型数值的运算
     * 
     * @param left 左操作数（long类型）
     * @param right 右操作数（long类型）
     * @return 运算结果（long类型）
     */
    default long apply(long left, long right) {
        return apply(BigInteger.valueOf(left), BigInteger.valueOf(right)).longValueExact();
    }

    /**
     * 执行两个float类型数值的运算
     * 
     * @param left 左操作数（float类型）
     * @param right 右操作数（float类型）
     * @return 运算结果（float类型）
     */
    default float apply(float left, float right) {
        return (float) apply((double) left, (double) right);
    }

    /**
     * 执行两个double类型数值的运算
     * 
     * @param left 左操作数（double类型）
     * @param right 右操作数（double类型）
     * @return 运算结果（double类型）
     */
    default double apply(double left, double right) {
        return apply(BigDecimal.valueOf(left), BigDecimal.valueOf(right)).doubleValue();
    }

    // ===== 高精度运算方法 =====
    
    /**
     * 执行两个BigInteger类型数值的运算
     * 
     * @param left 左操作数（BigInteger类型）
     * @param right 右操作数（BigInteger类型）
     * @return 运算结果（BigInteger类型）
     */
    default BigInteger apply(BigInteger left, BigInteger right) {
        BigDecimal value = apply(new BigDecimal(left), new BigDecimal(right));
        return value.toBigIntegerExact();
    }

    /**
     * 执行两个BigDecimal类型数值的运算（核心方法）
     * 
     * @param left 左操作数（BigDecimal类型）
     * @param right 右操作数（BigDecimal类型）
     * @return 运算结果（BigDecimal类型）
     */
    BigDecimal apply(BigDecimal left, BigDecimal right);

    // ===== 类型转换工具 =====
    
    /**
     * 将Number类型转换为BigDecimal类型
     * 
     * @param number 要转换的数值
     * @return 转换后的BigDecimal对象，确保精度不丢失
     */
    default BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal)
            return (BigDecimal) number;
        if (number instanceof BigInteger)
            return new BigDecimal((BigInteger) number);
        return new BigDecimal(number.toString());
    }

    /**
     * 将Number类型转换为BigInteger类型
     * 
     * @param number 要转换的数值（应为整数类型）
     * @return 转换后的BigInteger对象
     */
    default BigInteger toBigInteger(Number number) {
        if (number instanceof BigInteger)
            return (BigInteger) number;
        return BigInteger.valueOf(number.longValue());
    }
}