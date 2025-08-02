package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;

/**
 * 数值值抽象基类，实现{@link Number}和{@link Version}接口，提供统一的数值操作和类型转换能力。
 * 该抽象类支持高精度数值计算，提供类型安全的数值转换，并定义了基本的数学运算方法，
 * 是框架中数值处理的核心抽象。
 *
 * <p>核心特性：
 * <ul>
 *   <li>高精度计算：基于{@link BigDecimal}和{@link BigInteger}实现高精度数值运算</li>
 *   <li>类型安全转换：支持数值到基本类型和高精度类型的安全转换，包含范围检查</li>
 *   <li>基本运算：提供加、减、乘、除、取余、绝对值、幂等基本数学运算</li>
 *   <li>版本支持：实现Version接口，支持数值作为版本号的比较和表示</li>
 *   <li>常量定义：预定义常用数值常量（如ZERO、ONE、TEN等）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>金融计算：需要高精度和无精度丢失的货币计算</li>
 *   <li>科学计算：需要处理大数或高精度的科学计算场景</li>
 *   <li>数据验证：需要数值范围检查的验证逻辑</li>
 *   <li>版本控制：将数值作为版本号进行比较和管理</li>
 *   <li>算法实现：需要数值操作的各种算法实现</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建数值实例
 * NumberValue num = new BigDecimalValue("123.45");
 * 
 * // 基本运算
 * NumberValue result = num.add(new IntValue(10)).multiply(new IntValue(2));
 * 
 * // 类型转换
 * double doubleValue = num.getAsDouble();
 * BigInteger bigInteger = num.getAsBigInteger();
 * </pre>
 *
 * @see BigDecimalValue
 * @see IntValue
 * @see Version
 * @see Number
 */
public abstract class NumberValue extends Number implements Version {
    private static final long serialVersionUID = 1L;

    /** 数值-1的常量表示 */
    public static final NumberValue MINUS_ONE = new IntValue(-1);
    /** 数值0的常量表示 */
    public static final NumberValue ZERO = new IntValue(0);
    /** 数值1的常量表示 */
    public static final NumberValue ONE = new IntValue(1);
    /** 数值10的常量表示 */
    public static final NumberValue TEN = new IntValue(10);

    /**
     * 创建数值过大异常，当数值超过目标类型范围时抛出。
     *
     * @param number 超出范围的数值
     * @return 算术异常实例
     */
    private static RuntimeException createTooHighException(Number number) {
        return new ArithmeticException("The value[" + number + "] is too high");
    }

    /**
     * 获取数值的绝对值。
     * <p>
     * 子类必须实现此方法，返回当前数值的绝对值。
     *
     * @return 绝对值对应的NumberValue实例
     */
    public abstract NumberValue abs();

    /**
     * 执行加法运算。
     * <p>
     * 子类必须实现此方法，返回当前数值与指定数值的和。
     *
     * @param value 加数
     * @return 加法运算结果
     */
    public abstract NumberValue add(NumberValue value);

    /**
     * 执行除法运算。
     * <p>
     * 子类必须实现此方法，返回当前数值除以指定数值的商。
     *
     * @param value 除数
     * @return 除法运算结果
     */
    public abstract NumberValue divide(NumberValue value);

    /**
     * 返回数值的double表示，委托给{@link #getAsDouble()}方法。
     *
     * @return double类型的数值
     */
    @Override
    public double doubleValue() {
        return getAsDouble();
    }

    /**
     * 判断数值是否相等，基于数值的比较结果。
     * <p>
     * 若对象为NumberValue实例，比较两者的数值；否则直接比较对象。
     *
     * @param obj 待比较对象
     * @return true如果数值相等，false否则
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof NumberValue) {
            return compareTo((NumberValue) obj) == 0;
        }
        return false;
    }
    
    @Override
    public abstract int hashCode();
    
    /**
     * 返回数值的float表示，委托给{@link #getAsFloat()}方法。
     *
     * @return float类型的数值
     */
    @Override
    public float floatValue() {
        return getAsFloat();
    }

    /**
     * 获取数值的BigDecimal表示。
     * <p>
     * 子类必须实现此方法，返回高精度的BigDecimal表示。
     *
     * @return BigDecimal类型的数值
     */
    @Override
    public abstract BigDecimal getAsBigDecimal();

    /**
     * 获取数值的BigInteger表示。
     * <p>
     * 子类必须实现此方法，返回高精度的BigInteger表示。
     *
     * @return BigInteger类型的数值
     */
    @Override
    public abstract BigInteger getAsBigInteger();

    /**
     * 获取数值的布尔表示，非零为true，零为false。
     * <p>
     * 通过比较BigInteger表示与1的关系实现。
     *
     * @return true如果数值等于1，false否则
     */
    @Override
    public boolean getAsBoolean() {
        BigInteger number = getAsBigInteger();
        if (number == null) {
            return false;
        }

        return number.compareTo(BigInteger.ONE) == 0;
    }

    /**
     * 获取数值的byte表示，包含范围检查。
     * <p>
     * 若数值超过byte范围（-128~127），抛出ArithmeticException。
     *
     * @return byte类型的数值
     * @throws ArithmeticException 如果数值超过byte范围
     */
    @Override
    public byte getAsByte() throws ArithmeticException {
        BigInteger number = getAsBigInteger();
        if (number == null) {
            return 0;
        }

        if (number.compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) > 0) {
            throw createTooHighException(number);
        }
        return number.byteValue();
    }

    /**
     * 获取数值的char表示，委托给{@link #getAsByte()}方法。
     * <p>
     * 本质是将byte值转换为char。
     *
     * @return char类型的数值
     * @throws ArithmeticException 如果数值超过byte范围
     */
    @Override
    public char getAsChar() throws ArithmeticException {
        return (char) getAsByte();
    }

    /**
     * 获取数值的double表示，包含范围检查。
     * <p>
     * 若数值超过double范围，抛出ArithmeticException。
     *
     * @return double类型的数值
     * @throws ArithmeticException 如果数值超过double范围
     */
    @Override
    public double getAsDouble() throws ArithmeticException {
        BigDecimal number = getAsBigDecimal();
        if (number == null) {
            return 0;
        }
        if (number.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw createTooHighException(number);
        }
        return number.doubleValue();
    }

    /**
     * 获取数值的元素集合，始终返回包含自身的单元素集合。
     *
     * @return 包含当前数值的元素集合
     */
    @Override
    public Elements<? extends Value> getAsElements() {
        return Elements.singleton(this);
    }

    /**
     * 获取数值的float表示，包含范围检查。
     * <p>
     * 若数值超过float范围，抛出ArithmeticException。
     *
     * @return float类型的数值
     * @throws ArithmeticException 如果数值超过float范围
     */
    @Override
    public float getAsFloat() throws ArithmeticException {
        BigDecimal number = getAsBigDecimal();
        if (number == null) {
            return 0;
        }

        if (number.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
            throw createTooHighException(number);
        }
        return number.floatValue();
    }

    /**
     * 获取数值的int表示，包含范围检查。
     * <p>
     * 若数值超过int范围（-2^31~2^31-1），抛出ArithmeticException。
     *
     * @return int类型的数值
     * @throws ArithmeticException 如果数值超过int范围
     */
    @Override
    public int getAsInt() throws ArithmeticException {
        BigInteger number = getAsBigInteger();
        if (number == null) {
            return 0;
        }

        if (number.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw createTooHighException(number);
        }
        return number.intValue();
    }

    /**
     * 获取数值的long表示，包含范围检查。
     * <p>
     * 若数值超过long范围（-2^63~2^63-1），抛出ArithmeticException。
     *
     * @return long类型的数值
     * @throws ArithmeticException 如果数值超过long范围
     */
    @Override
    public long getAsLong() throws ArithmeticException {
        BigInteger number = getAsBigInteger();
        if (number == null) {
            return 0;
        }

        if (number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            throw createTooHighException(number);
        }
        return number.longValue();
    }

    /**
     * 获取数值的NumberValue表示，直接返回自身。
     *
     * @return 当前数值实例
     */
    @Override
    public NumberValue getAsNumber() {
        return this;
    }

    /**
     * 获取数值的short表示，包含范围检查。
     * <p>
     * 若数值超过short范围（-32768~32767），抛出ArithmeticException。
     *
     * @return short类型的数值
     * @throws ArithmeticException 如果数值超过short范围
     */
    @Override
    public short getAsShort() throws ArithmeticException {
        BigInteger number = getAsBigInteger();
        if (number == null) {
            return 0;
        }

        if (number.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0) {
            throw createTooHighException(number);
        }
        return number.shortValue();
    }

    /**
     * 返回数值的int表示，委托给{@link #getAsInt()}方法。
     *
     * @return int类型的数值
     */
    @Override
    public int intValue() {
        return getAsInt();
    }

    /**
     * 判断是否为多值类型，数值始终为单值类型。
     *
     * @return false
     */
    @Override
    public final boolean isMultiple() {
        return false;
    }

    /**
     * 判断是否为数字类型，始终返回true。
     *
     * @return true
     */
    @Override
    public boolean isNumber() {
        return true;
    }

    /**
     * 返回数值的long表示，委托给{@link #getAsLong()}方法。
     *
     * @return long类型的数值
     */
    @Override
    public long longValue() {
        return getAsLong();
    }

    /**
     * 执行乘法运算。
     * <p>
     * 子类必须实现此方法，返回当前数值与指定数值的乘积。
     *
     * @param value 乘数
     * @return 乘法运算结果
     */
    public abstract NumberValue multiply(NumberValue value);

    /**
     * 执行指数运算。
     * <p>
     * 子类必须实现此方法，返回当前数值的指定次幂。
     *
     * @param value 指数
     * @return 指数运算结果
     */
    public abstract NumberValue pow(NumberValue value);

    /**
     * 执行取余运算。
     * <p>
     * 子类必须实现此方法，返回当前数值除以指定数值的余数。
     *
     * @param value 除数
     * @return 取余运算结果
     */
    public abstract NumberValue remainder(NumberValue value);

    /**
     * 执行减法运算。
     * <p>
     * 子类必须实现此方法，返回当前数值减去指定数值的差。
     *
     * @param value 减数
     * @return 减法运算结果
     */
    public abstract NumberValue subtract(NumberValue value);

    /**
     * 返回数值的字符串表示，委托给{@link #getAsString()}方法。
     *
     * @return 数值的字符串表示
     */
    @Override
    public String toString() {
        return getAsString();
    }
}