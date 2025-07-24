package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.Value;

/**
 * 整数数值实现类，用于表示和处理整数类型的数值。
 * 该类继承自{@link RationalNumber}，提供了整数的标准数学运算和转换功能，
 * 所有操作均基于Java原生int类型实现，确保高效的数值处理。
 *
 * <p>核心特性：
 * <ul>
 *   <li>原生int支持：使用Java原生int类型存储数值，确保高效运算</li>
 *   <li>高精度转换：支持转换为BigDecimal和BigInteger进行高精度计算</li>
 *   <li>接口实现：实现了{@link Value}接口，提供统一的数值操作方法</li>
 *   <li>不可变性：实例一旦创建，其值不可更改，保证线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>整数运算：需要高效处理整数的数学计算</li>
 *   <li>数值存储：需要以对象形式存储整数的场景</li>
 *   <li>框架集成：与需要实现{@link Value}接口的框架组件集成</li>
 *   <li>泛型支持：在需要处理泛型数值的场景中使用</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建IntValue实例
 * IntValue intValue = new IntValue(42);
 * 
 * // 基本运算
 * NumberValue sum = intValue.add(new IntValue(10));  // 42 + 10 = 52
 * NumberValue product = intValue.multiply(new IntValue(2));  // 42 * 2 = 84
 * 
 * // 转换为其他类型
 * BigDecimal bigDecimalValue = intValue.getAsBigDecimal();  // 42.0
 * BigInteger bigIntegerValue = intValue.getAsBigInteger();  // 42
 * 
 * // 比较操作
 * int comparisonResult = intValue.compareTo(new IntValue(50));  // 返回负数，表示小于
 * </pre>
 *
 * @author soeasy.run
 * @see RationalNumber
 * @see NumberValue
 * @see Value
 */
@RequiredArgsConstructor
public class IntValue extends RationalNumber {
    private static final long serialVersionUID = 1L;
    private final int value;

    /**
     * 将当前整数转换为BigDecimal表示。
     * <p>
     * 该方法返回一个新的BigDecimal实例，其值等于当前整数。
     * 适用于需要高精度计算的场景。
     *
     * @return 表示当前整数的BigDecimal
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return new BigDecimal(value);
    }

    /**
     * 将当前整数转换为BigInteger表示。
     * <p>
     * 该方法返回一个新的BigInteger实例，其值等于当前整数。
     * 适用于需要大整数计算的场景。
     *
     * @return 表示当前整数的BigInteger
     */
    @Override
    public BigInteger getAsBigInteger() {
        return new BigInteger("" + value);
    }

    /**
     * 获取当前整数的原始int值。
     * <p>
     * 该方法直接返回封装的int值，不进行任何转换，确保高效访问。
     *
     * @return 当前整数的int值
     */
    @Override
    public int getAsInt() {
        return value;
    }

    /**
     * 比较当前整数与另一个值的大小。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>如果另一个值是数值类型，将其转换为BigInteger进行比较</li>
     *   <li>如果另一个值的BigInteger表示超过Integer.MAX_VALUE，返回-1（小于）</li>
     *   <li>否则直接比较两个整数的大小</li>
     *   <li>如果另一个值不是数值类型，调用父类的比较方法</li>
     * </ol>
     *
     * @param o 要比较的值
     * @return 比较结果：负整数表示小于，零表示等于，正整数表示大于
     */
    @Override
    public int compareTo(Value o) {
        if (o.isNumber()) {
            BigInteger otherValue = o.getAsBigInteger();
            // 处理溢出情况：如果另一个值大于Integer.MAX_VALUE，当前值肯定小于它
            if (otherValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                return -1;
            }
            // 处理溢出情况：如果另一个值小于Integer.MIN_VALUE，当前值肯定大于它
            if (otherValue.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
                return 1;
            }
            return Integer.compare(this.value, otherValue.intValue());
        }
        return super.compareTo(o);
    }

    /**
     * 将当前整数转换为字符串表示。
     * <p>
     * 该方法返回当前整数的十进制字符串表示。
     * 例如，整数42将返回字符串"42"。
     *
     * @return 当前整数的字符串表示
     */
    @Override
    public String getAsString() {
        return Integer.toString(value);
    }
    
    @Override
    public int hashCode() {
    	return value;
    }
}