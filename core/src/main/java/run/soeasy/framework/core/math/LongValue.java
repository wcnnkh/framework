package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.Value;

/**
 * 长整数数值实现类，用于表示和处理长整数类型的数值。
 * 该类继承自{@link RationalNumber}，提供了长整数的标准数学运算和转换功能，
 * 所有操作均基于Java原生long类型实现，确保高效的数值处理和更大的数值范围。
 *
 * <p>核心特性：
 * <ul>
 *   <li>原生long支持：使用Java原生long类型存储数值，支持更大范围的整数</li>
 *   <li>高精度转换：支持转换为BigDecimal和BigInteger进行高精度计算</li>
 *   <li>接口实现：实现了{@link Value}接口，提供统一的数值操作方法</li>
 *   <li>不可变性：实例一旦创建，其值不可更改，保证线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>大范围整数运算：需要处理超过int范围的整数计算</li>
 *   <li>时间戳处理：存储和处理时间戳等长整数值</li>
 *   <li>框架集成：与需要实现{@link Value}接口的框架组件集成</li>
 *   <li>泛型支持：在需要处理泛型数值的场景中使用</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建LongValue实例
 * LongValue longValue = new LongValue(9223372036854775807L);
 * 
 * // 基本运算
 * NumberValue sum = longValue.add(new LongValue(10));  // 9223372036854775807 + 10 = 9223372036854775817
 * 
 * // 转换为其他类型
 * BigDecimal bigDecimalValue = longValue.getAsBigDecimal();  // 9223372036854775807.0
 * BigInteger bigIntegerValue = longValue.getAsBigInteger();  // 9223372036854775807
 * 
 * // 比较操作
 * int comparisonResult = longValue.compareTo(new LongValue(9223372036854775800L));  // 返回正数，表示大于
 * </pre>
 *
 * @author shuchaowen
 * @see RationalNumber
 * @see NumberValue
 * @see Value
 */
@RequiredArgsConstructor
public class LongValue extends RationalNumber {
    private static final long serialVersionUID = 1L;
    private final long value;

    /**
     * 将当前长整数转换为BigDecimal表示。
     * <p>
     * 该方法返回一个新的BigDecimal实例，其值等于当前长整数。
     * 适用于需要高精度计算的场景。
     *
     * @return 表示当前长整数的BigDecimal
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return new BigDecimal(value);
    }

    /**
     * 将当前长整数转换为BigInteger表示。
     * <p>
     * 该方法返回一个新的BigInteger实例，其值等于当前长整数。
     * 适用于需要大整数计算的场景。
     *
     * @return 表示当前长整数的BigInteger
     */
    @Override
    public BigInteger getAsBigInteger() {
        return new BigInteger(value + "");
    }

    /**
     * 获取当前长整数的原始long值。
     * <p>
     * 该方法直接返回封装的long值，不进行任何转换，确保高效访问。
     *
     * @return 当前长整数的long值
     */
    @Override
    public long getAsLong() {
        return value;
    }

    /**
     * 将当前长整数转换为字符串表示。
     * <p>
     * 该方法返回当前长整数的十进制字符串表示。
     * 例如，长整数9223372036854775807L将返回字符串"9223372036854775807"。
     *
     * @return 当前长整数的字符串表示
     */
    @Override
    public String getAsString() {
        return Long.toString(value);
    }

    /**
     * 比较当前长整数与另一个值的大小。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>如果另一个值是数值类型，将其转换为BigInteger进行比较</li>
     *   <li>如果另一个值的BigInteger表示超过Long.MAX_VALUE，返回-1（当前值小于它）</li>
     *   <li>否则调用父类的比较方法进行详细比较</li>
     * </ol>
     *
     * @param o 要比较的值
     * @return 比较结果：负整数表示小于，零表示等于，正整数表示大于
     */
    @Override
    public int compareTo(Value o) {
        if (o.isNumber()) {
            BigInteger otherValue = o.getAsBigInteger();
            // 处理溢出情况：如果另一个值大于Long.MAX_VALUE，当前值肯定小于它
            if (otherValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                return -1;
            }
            // 处理溢出情况：如果另一个值小于Long.MIN_VALUE，当前值肯定大于它
            if (otherValue.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                return 1;
            }
        }
        return super.compareTo(o);
    }
    
    @Override
    public int hashCode() {
    	return (int) value;
    }
}