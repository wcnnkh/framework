package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptorWrapper;

/**
 * 属性包装器接口，继承自{@link Property}和{@link PropertyDescriptorWrapper}，
 * 采用装饰器模式实现对目标属性的行为包装或扩展，支持在不修改原属性的前提下增强其功能。
 * <p>
 * 该接口定义了属性包装的标准规范，默认方法将所有操作委托给被包装的源属性，
 * 子类可通过覆盖特定方法修改属性的读写行为、类型转换逻辑或元数据描述。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有{@link Property}实例扩展功能</li>
 *   <li>行为委托：默认方法将操作转发给被包装的源属性</li>
 *   <li>功能扩展：可覆盖特定方法实现属性访问控制、类型转换等增强功能</li>
 *   <li>不可变性保证：包装器应保持源属性的不可变特性</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的源属性类型，需实现{@link Property}接口</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>属性访问控制（如只读属性的写操作拦截）</li>
 *   <li>值转换增强（如属性值的自动类型转换）</li>
 *   <li>访问日志记录（记录属性读写操作）</li>
 *   <li>元数据修改（临时修改属性名称或描述）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Property
 * @see PropertyDescriptorWrapper
 * @see AccessibleDescriptorWrapper
 */
@FunctionalInterface
public interface PropertyWrapper<W extends Property> extends Property, PropertyDescriptorWrapper<W> {
    
    /**
     * 从目标对象读取属性值（默认委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link Property#readFrom(Object)}方法，
     * 子类可覆盖此方法实现自定义读取逻辑（如值转换、访问控制）。
     * 
     * @param target 目标对象
     * @return 属性值
     */
    @Override
    default Object readFrom(Object target) {
        return getSource().readFrom(target);
    }

    /**
     * 向目标对象写入属性值（默认委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link Property#writeTo(Object, Object)}方法，
     * 子类可覆盖此方法实现自定义写入逻辑（如值验证、类型转换）。
     * 
     * @param target 目标对象
     * @param value 要写入的值
     */
    @Override
    default void writeTo(Object target, Object value) {
        getSource().writeTo(target, value);
    }

    /**
     * 判断属性是否可读（默认委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link Property#isReadable()}方法，
     * 子类可覆盖此方法临时修改属性的可读状态。
     * 
     * @return 属性可读返回true，否则false
     */
    @Override
    default boolean isReadable() {
        return getSource().isReadable();
    }

    /**
     * 判断属性是否可写（默认委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link Property#isWriteable()}方法，
     * 子类可覆盖此方法临时修改属性的可写状态。
     * 
     * @return 属性可写返回true，否则false
     */
    @Override
    default boolean isWriteable() {
        return getSource().isWriteable();
    }

    /**
     * 获取属性访问器（默认委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link Property#accessor(Object)}方法，
     * 子类可覆盖此方法返回自定义的属性访问器实例。
     * 
     * @param target 目标对象
     * @return 属性访问器实例
     */
    @Override
    default PropertyAccessor accessor(Object target) {
        return getSource().accessor(target);
    }
}