package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * 高性能数字比较器，支持不同类型Number对象的大小比较。
 * 该比较器会优先使用最小必要精度进行比较，仅在必要时才转换为高精度类型，
 * 从而在保证准确性的同时提升性能。
 *
 * <p>实现特点：
 * <ul>
 *   <li>单例模式：通过{@link #DEFAULT}提供全局唯一实例</li>
 *   <li>类型安全：正确处理不同Number子类的比较逻辑</li>
 *   <li>空值处理：null值被视为小于非null值</li>
 *   <li>性能优化：优先使用基本数据类型比较，避免不必要的高精度计算</li>
 * </ul>
 *
 * <p>比较策略：
 * <ol>
 *   <li>null值小于任何非null值</li>
 *   <li>相同对象或equals返回true时视为相等</li>
 *   <li>优先使用相同类型或基本类型比较（如int/long/double）</li>
 *   <li>仅在必要时转换为高精度类型（BigInteger/BigDecimal）</li>
 * </ol>
 *
 * @author soeasy.run
 */
public class NumberComparator implements Comparator<Number> {

    /**
     * 比较器单例实例，推荐使用此实例而非创建新对象
     */
    public static final NumberComparator DEFAULT = new NumberComparator();

    /**
     * 比较两个Number对象的大小
     * 
     * @param left 左操作数
     * @param right 右操作数
     * @return 比较结果：
     *         - 负数：left &lt; right
     *         - 零：left == right
     *         - 正数：left &gt; right
     */
    @Override
    public int compare(Number left, Number right) {
        // 处理null值情况
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }

        // 处理相等情况（包括值相等但类型不同的情况）
        if (left.equals(right)) {
            return 0;
        }

        // 快速路径：相同基本类型直接比较
        if (left.getClass() == right.getClass()) {
            return compareSameType(left, right);
        }

        // 混合类型比较策略
        if (left instanceof BigDecimal || right instanceof BigDecimal) {
            return compareWithBigDecimal(left, right);
        }

        if (left instanceof BigInteger || right instanceof BigInteger) {
            return compareWithBigInteger(left, right);
        }

        // 处理基本浮点类型
        if (isFloatingPoint(left) || isFloatingPoint(right)) {
            return Double.compare(left.doubleValue(), right.doubleValue());
        }

        // 处理基本整数类型（自动提升为long）
        return Long.compare(left.longValue(), right.longValue());
    }

    /**
     * 判断是否为浮点类型
     */
    private boolean isFloatingPoint(Number num) {
        return num instanceof Float || num instanceof Double;
    }

    /**
     * 相同类型的快速比较
     */
    @SuppressWarnings("unchecked")
	private int compareSameType(Number left, Number right) {
    	if(left instanceof Comparable) {
        	return ((Comparable<Number>) left).compareTo(right);
        }
        // 其他Number子类使用默认比较（通常基于double值）
        return Double.compare(left.doubleValue(), right.doubleValue());
    }

    /**
     * 使用BigDecimal进行高精度比较
     */
    private int compareWithBigDecimal(Number left, Number right) {
        BigDecimal leftBD = toBigDecimal(left);
        BigDecimal rightBD = toBigDecimal(right);
        return leftBD.compareTo(rightBD);
    }

    /**
     * 使用BigInteger进行高精度整数比较
     */
    private int compareWithBigInteger(Number left, Number right) {
        // 检查是否可以安全转换为long进行比较
        if (canConvertToLong(left) && canConvertToLong(right)) {
            return Long.compare(left.longValue(), right.longValue());
        }

        // 否则使用BigInteger
        BigInteger leftBI = toBigInteger(left);
        BigInteger rightBI = toBigInteger(right);
        return leftBI.compareTo(rightBI);
    }

    /**
     * 判断Number是否可以安全转换为long而不丢失精度
     */
    private boolean canConvertToLong(Number num) {
        if (num instanceof BigInteger) {
            BigInteger bi = (BigInteger) num;
            return bi.bitLength() <= 63; // long范围: -2^63 ~ 2^63-1
        }
        if (num instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) num;
            return bd.stripTrailingZeros().scale() <= 0 && 
                   bd.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) >= 0 &&
                   bd.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0;
        }
        if (isFloatingPoint(num)) {
            double d = num.doubleValue();
            return d >= Long.MIN_VALUE && d <= Long.MAX_VALUE && 
                   (double) (long) d == d; // 检查是否为整数
        }
        return true; // 其他类型（如int/long/short/byte）都可以安全转换为long
    }

    /**
     * 将Number转换为BigDecimal
     */
    private BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }
        if (number instanceof Double) {
            return new BigDecimal(Double.toString((Double) number));
        }
        if (number instanceof Float) {
            return new BigDecimal(Float.toString((Float) number));
        }
        return new BigDecimal(number.toString());
    }

    /**
     * 将Number转换为BigInteger
     */
    private BigInteger toBigInteger(Number number) {
        if (number instanceof BigInteger) {
            return (BigInteger) number;
        }
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        }
        return BigInteger.valueOf(number.longValue());
    }
}