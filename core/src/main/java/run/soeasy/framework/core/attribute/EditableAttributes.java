package run.soeasy.framework.core.attribute;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * 可编辑属性接口，扩展Attributes接口，支持属性的修改操作。
 * 该接口定义了设置和移除属性的方法，适用于需要动态管理属性的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承Attributes接口，支持属性的读取操作</li>
 *   <li>提供属性设置和移除的能力，实现属性的动态管理</li>
 *   <li>默认实现setAttributes方法，支持批量设置属性</li>
 *   <li>支持泛型类型，可灵活定义属性键值的类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>配置信息的动态修改</li>
 *   <li>运行时环境变量的管理</li>
 *   <li>组件属性的初始化和更新</li>
 *   <li>请求/响应头信息的动态设置</li>
 * </ul>
 *
 * @param <K> 属性键的类型
 * @param <V> 属性值的类型
 * @see Attributes
 * @see EditableAttributesWrapper
 */
public interface EditableAttributes<K, V> extends Attributes<K, V> {

    /**
     * 可编辑属性包装器接口，用于对EditableAttributes实例进行透明包装。
     * 该接口继承自EditableAttributes和AttributesWrapper，
     * 提供了对底层EditableAttributes的操作委托功能。
     *
     * @param <K> 属性键的类型
     * @param <V> 属性值的类型
     * @param <W> 被包装的EditableAttributes类型
     */
    public interface EditableAttributesWrapper<K, V, W extends EditableAttributes<K, V>>
            extends EditableAttributes<K, V>, AttributesWrapper<K, V, W> {

        /**
         * 批量设置属性，委托给源EditableAttributes的setAttributes方法。
         * 该方法会将指定Attributes中的所有属性复制到此包装器中。
         *
         * @param attributes 包含要设置属性的Attributes实例
         */
        @Override
        default void setAttributes(Attributes<K, ? extends V> attributes) {
            getSource().setAttributes(attributes);
        }

        /**
         * 设置单个属性值，委托给源EditableAttributes的setAttribute方法。
         *
         * @param name  属性名称
         * @param value 属性值
         */
        @Override
        default void setAttribute(K name, V value) {
            getSource().setAttribute(name, value);
        }

        /**
         * 移除指定名称的属性，委托给源EditableAttributes的removeAttribute方法。
         *
         * @param name 要移除的属性名称
         */
        @Override
        default void removeAttribute(K name) {
            getSource().removeAttribute(name);
        }
    }

    /**
     * 设置属性值。
     * 若属性已存在，将覆盖原有值；若不存在，则新增该属性。
     *
     * @param name  属性名称，不可为null
     * @param value 属性值，可为null（取决于具体实现是否支持）
     */
    void setAttribute(K name, V value);

    /**
     * 移除指定名称的属性。
     * 若属性不存在，通常不做处理，但具体实现可能有不同行为。
     *
     * @param name 要移除的属性名称，不可为null
     */
    void removeAttribute(K name);

    /**
     * 批量设置属性。
     * 默认实现遍历源Attributes中的所有属性并逐个调用setAttribute方法。
     * 实现类可根据需要重写此方法以提供更高效的批量操作。
     *
     * @param attributes 包含要设置属性的Attributes实例，不可为null
     */
    default void setAttributes(Attributes<K, ? extends V> attributes) {
    	Streamable<? extends K> keys = attributes.getAttributeNames();
        keys.forEach((key) -> setAttribute(key, attributes.getAttribute(key)));
    }
}