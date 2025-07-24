package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;

/**
 * 大整数数值实现类，用于表示任意精度的整数数值。
 * 该类封装了Java的{@link BigInteger}，实现了{@link Value}接口，
 * 提供了高精度整数的比较、转换和计算能力。
 *
 * <p>特性：
 * <ul>
 *   <li>不可变对象，线程安全</li>
 *   <li>支持与其他数值类型的比较和运算</li>
 *   <li>提供零值({@link #ZERO})和一值({@link #ONE})的静态实例</li>
 * </ul>
 *
 * <p>主要用途：
 * <ul>
 *   <li>处理超出long范围的大整数</li>
 *   <li>需要高精度计算的场景</li>
 *   <li>实现自定义数值类型的基础组件</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.math.BigInteger
 * @see run.soeasy.framework.core.domain.Value
 */
public class BigIntegerValue extends RationalNumber {
    private static final long serialVersionUID = 1L;

    /**
     * 表示数值零的常量实例
     */
    public static final BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);

    /**
     * 表示数值一的常量实例
     */
    public static final BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);

    /**
     * 封装的大整数值
     */
    private BigInteger value;

    /**
     * 使用指定的BigInteger值创建实例
     * 
     * @param value 大整数值，不可为null
     * @throws NullPointerException 如果value为null
     */
    public BigIntegerValue(BigInteger value) {
        this.value = value;
    }

    /**
     * 与另一个Value对象进行比较
     * 
     * @param o 要比较的对象
     * @return 如果o是数值类型，返回内部BigInteger的比较结果；
     *         否则调用父类的比较方法
     */
    @Override
    public int compareTo(Value o) {
        if (o.isNumber()) {
            BigInteger value = o.getAsBigInteger();
            return this.value.compareTo(value);
        }
        return super.compareTo(o);
    }

    /**
     * 返回此实例的哈希码，基于内部BigInteger值计算
     * 
     * @return 哈希码值
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * 将此大整数值转换为BigDecimal
     * 
     * @return 转换后的BigDecimal对象
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return new BigDecimal(value);
    }

    /**
     * 获取内部封装的BigInteger值
     * 
     * @return 此实例表示的BigInteger值
     */
    @Override
    public BigInteger getAsBigInteger() {
        return value;
    }

    /**
     * 将此大整数值转换为字符串表示
     * 
     * @return 大整数的字符串形式
     */
    @Override
    public String getAsString() {
        return value.toString();
    }
}