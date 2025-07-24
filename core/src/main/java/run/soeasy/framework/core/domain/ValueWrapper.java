package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;

/**
 * 值包装器接口，用于包装{@link Value}实例并委托所有操作，
 * 实现装饰器模式以支持对值操作的透明增强。该接口继承自{@link Value}和{@link Wrapper}，
 * 允许在不修改原始值对象的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发给被包装的{@link Value}实例</li>
 *   <li>装饰扩展：支持通过包装器添加日志记录、缓存、验证等额外功能</li>
 *   <li>类型安全：通过泛型确保包装器与被包装值的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式创建轻量级包装器</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>值访问日志记录：记录所有值获取操作的访问日志</li>
 *   <li>值转换缓存：缓存频繁访问的值转换结果</li>
 *   <li>值验证增强：在获取值前进行合法性验证</li>
 *   <li>事务性值操作：为值操作添加事务边界</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 包装值并添加日志记录
 * Value originalValue = config.getValue("timeout");
 * ValueWrapper<Value> loggedValue = value -> {
 *     System.out.println("Access value: " + value.getAsString());
 *     return originalValue;
 * };
 * int timeout = loggedValue.getAsInt(); // 访问时打印日志
 * </pre>
 *
 * @param <W> 被包装的值类型，必须是{@link Value}的子类型
 * @see Value
 * @see Wrapper
 */
@FunctionalInterface
public interface ValueWrapper<W extends Value> extends Value, Wrapper<W> {
    /**
     * 获取包装值的BigDecimal表示，转发给被包装的Value实例。
     *
     * @return 被包装值的BigDecimal表示
     * @see Value#getAsBigDecimal()
     */
    @Override
    default BigDecimal getAsBigDecimal() {
        return getSource().getAsBigDecimal();
    }

    /**
     * 获取包装值的BigInteger表示，转发给被包装的Value实例。
     *
     * @return 被包装值的BigInteger表示
     * @see Value#getAsBigInteger()
     */
    @Override
    default BigInteger getAsBigInteger() {
        return getSource().getAsBigInteger();
    }

    /**
     * 获取包装值的布尔表示，转发给被包装的Value实例。
     *
     * @return 被包装值的布尔表示，默认false
     * @see Value#getAsBoolean()
     */
    @Override
    default boolean getAsBoolean() {
        return getSource().getAsBoolean();
    }

    /**
     * 获取包装值的字节表示，转发给被包装的Value实例。
     *
     * @return 被包装值的字节表示，默认0
     * @see Value#getAsByte()
     */
    @Override
    default byte getAsByte() {
        return getSource().getAsByte();
    }

    /**
     * 获取包装值的字符表示，转发给被包装的Value实例。
     *
     * @return 被包装值的字符表示，默认0
     * @see Value#getAsChar()
     */
    @Override
    default char getAsChar() {
        return getSource().getAsChar();
    }

    /**
     * 获取包装值的双精度浮点表示，转发给被包装的Value实例。
     *
     * @return 被包装值的双精度浮点表示，默认0d
     * @see Value#getAsDouble()
     */
    @Override
    default double getAsDouble() {
        return getSource().getAsDouble();
    }

    /**
     * 获取包装值的元素集合表示，转发给被包装的Value实例。
     *
     * @return 被包装值的元素集合，默认空集合
     * @see Value#getAsElements()
     */
    @Override
    default Elements<? extends Value> getAsElements() {
        return getSource().getAsElements();
    }

    /**
     * 将包装值转换为枚举类型，转发给被包装的Value实例。
     *
     * @param enumType 枚举类型
     * @param <T>      枚举类型参数
     * @return 转换后的枚举值
     * @see Value#getAsEnum(Class)
     */
    @Override
    default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
        return getSource().getAsEnum(enumType);
    }

    /**
     * 获取包装值的单精度浮点表示，转发给被包装的Value实例。
     *
     * @return 被包装值的单精度浮点表示，默认0f
     * @see Value#getAsFloat()
     */
    @Override
    default float getAsFloat() {
        return getSource().getAsFloat();
    }

    /**
     * 获取包装值的整数表示，转发给被包装的Value实例。
     *
     * @return 被包装值的整数表示，默认0
     * @see Value#getAsInt()
     */
    @Override
    default int getAsInt() {
        return getSource().getAsInt();
    }

    /**
     * 获取包装值的长整数表示，转发给被包装的Value实例。
     *
     * @return 被包装值的长整数表示，默认0L
     * @see Value#getAsLong()
     */
    @Override
    default long getAsLong() {
        return getSource().getAsLong();
    }

    /**
     * 获取包装值的数字表示，转发给被包装的Value实例。
     *
     * @return 被包装值的数字表示
     * @see Value#getAsNumber()
     */
    @Override
    default NumberValue getAsNumber() {
        return getSource().getAsNumber();
    }

    /**
     * 获取包装值的短整数表示，转发给被包装的Value实例。
     *
     * @return 被包装值的短整数表示，默认0
     * @see Value#getAsShort()
     */
    @Override
    default short getAsShort() {
        return getSource().getAsShort();
    }

    /**
     * 获取包装值的字符串表示，转发给被包装的Value实例。
     *
     * @return 被包装值的字符串表示
     * @see Value#getAsString()
     */
    @Override
    default String getAsString() {
        return getSource().getAsString();
    }

    /**
     * 获取包装值的版本表示，转发给被包装的Value实例。
     *
     * @return 被包装值的版本表示
     * @see Value#getAsVersion()
     */
    @Override
    default Version getAsVersion() {
        return getSource().getAsVersion();
    }

    /**
     * 判断包装值是否为多值类型，转发给被包装的Value实例。
     *
     * @return 被包装值的多值类型判断结果
     * @see Value#isMultiple()
     */
    @Override
    default boolean isMultiple() {
        return getSource().isMultiple();
    }

    /**
     * 判断包装值是否为数字类型，转发给被包装的Value实例。
     *
     * @return 被包装值的数字类型判断结果
     * @see Value#isNumber()
     */
    @Override
    default boolean isNumber() {
        return getSource().isNumber();
    }
}