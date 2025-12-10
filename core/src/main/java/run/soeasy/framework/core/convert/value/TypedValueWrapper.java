package run.soeasy.framework.core.convert.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.NumberValue;
import run.soeasy.framework.core.domain.ValueWrapper;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 类型化值包装器接口，用于装饰{@link TypedValue}实现，支持透明代理和功能增强。
 * <p>
 * 该函数式接口继承自{@link TypedValue}、{@link TypedDataWrapper}和{@link ValueWrapper}，
 * 允许通过包装现有类型化值实例来添加额外逻辑，同时保持接口的透明性，
 * 适用于需要对类型化值进行非侵入式增强的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明代理：所有方法默认转发给被包装的类型化值实例</li>
 *   <li>功能增强：支持添加日志记录、缓存优化、类型转换增强等额外功能</li>
 *   <li>类型安全：泛型参数确保包装器与被包装对象的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式快速创建轻量级包装器</li>
 * </ul>
 *
 * @param <W> 被包装的类型化值类型，需实现{@link TypedValue}
 * 
 * @author soeasy.run
 * @see TypedValue
 * @see TypedDataWrapper
 * @see ValueWrapper
 */
@FunctionalInterface
public interface TypedValueWrapper<W extends TypedValue>
        extends TypedValue, TypedDataWrapper<Object, W>, ValueWrapper<W> {

    /**
     * 获取BigDecimal类型的值（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的类型化值实例，
     * 子类可重写此方法以添加自定义逻辑（如精度控制、舍入规则等）。
     * 
     * @return 被包装类型化值的BigDecimal表示
     */
    @Override
    default BigDecimal getAsBigDecimal() {
        return getSource().getAsBigDecimal();
    }

    /**
     * 获取BigInteger类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的BigInteger表示
     */
    @Override
    default BigInteger getAsBigInteger() {
        return getSource().getAsBigInteger();
    }

    /**
     * 获取boolean类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的boolean表示
     */
    @Override
    default boolean getAsBoolean() {
        return getSource().getAsBoolean();
    }

    /**
     * 获取byte类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的byte表示
     */
    @Override
    default byte getAsByte() {
        return getSource().getAsByte();
    }

    /**
     * 获取char类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的char表示
     */
    @Override
    default char getAsChar() {
        return getSource().getAsChar();
    }

    /**
     * 获取double类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的double表示
     */
    @Override
    default double getAsDouble() {
        return getSource().getAsDouble();
    }

    /**
     * 获取元素集合（透明代理实现）
     * 
     * @return 被包装类型化值的元素集合表示
     */
    @Override
    default Streamable<? extends TypedValue> getAsElements() {
        return getSource().getAsElements();
    }

    /**
     * 获取枚举类型的值（透明代理实现）
     * 
     * @param enumType 枚举类型
     * @return 被包装类型化值的枚举表示
     */
    @Override
    default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
        return getSource().getAsEnum(enumType);
    }

    /**
     * 获取float类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的float表示
     */
    @Override
    default float getAsFloat() {
        return getSource().getAsFloat();
    }

    /**
     * 获取int类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的int表示
     */
    @Override
    default int getAsInt() {
        return getSource().getAsInt();
    }

    /**
     * 获取long类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的long表示
     */
    @Override
    default long getAsLong() {
        return getSource().getAsLong();
    }

    /**
     * 获取NumberValue类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的NumberValue表示
     */
    @Override
    default NumberValue getAsNumber() {
        return getSource().getAsNumber();
    }

    /**
     * 获取short类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的short表示
     */
    @Override
    default short getAsShort() {
        return getSource().getAsShort();
    }

    /**
     * 获取String类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的String表示
     */
    @Override
    default String getAsString() {
        return getSource().getAsString();
    }

    /**
     * 获取Version类型的值（透明代理实现）
     * 
     * @return 被包装类型化值的Version表示
     */
    @Override
    default Version getAsVersion() {
        return getSource().getAsVersion();
    }

    /**
     * 判断是否为多值类型（透明代理实现）
     * 
     * @return 被包装类型化值的多值类型判断结果
     */
    @Override
    default boolean isMultiple() {
        return getSource().isMultiple();
    }

    /**
     * 判断是否为数字类型（透明代理实现）
     * 
     * @return 被包装类型化值的数字类型判断结果
     */
    @Override
    default boolean isNumber() {
        return getSource().isNumber();
    }

    /**
     * 返回当前值的TypedValue表示（透明代理实现）
     * 
     * @return 被包装类型化值的自身引用
     */
    default TypedValue value() {
        return getSource().value();
    }

    /**
     * 获取返回类型描述符（透明代理实现）
     * 
     * @return 被包装类型化值的类型描述符
     */
    @Override
    default TypeDescriptor getReturnTypeDescriptor() {
        return getSource().getReturnTypeDescriptor();
    }

    /**
     * 转换为指定类型的TypedData（透明代理实现）
     * 
     * @param <R>        目标类型
     * @param type       目标类型Class，不可为null
     * @param converter  转换器，不可为null
     * @return 转换后的TypedData实例
     */
    @Override
    default <R> TypedData<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
        return getSource().map(type, converter);
    }

    /**
     * 转换为指定类型的TypedValue（透明代理实现）
     * 
     * @param typeDescriptor 目标类型描述符，不可为null
     * @param converter      转换器，不可为null
     * @return 转换后的TypedValue实例
     */
    @Override
    default TypedValue map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
        return getSource().map(typeDescriptor, converter);
    }
}