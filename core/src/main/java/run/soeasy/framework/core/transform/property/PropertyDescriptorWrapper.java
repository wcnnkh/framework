package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptorWrapper;

/**
 * 属性描述符包装器接口，继承自{@link PropertyDescriptor}和{@link AccessibleDescriptorWrapper}，
 * 作为函数式接口支持通过lambda表达式创建包装器实例，实现对目标属性描述符的行为装饰或扩展。
 * <p>
 * 该接口定义了属性描述符的包装规范，默认方法将所有操作委托给被包装的源描述符，
 * 子类可通过覆盖方法修改特定行为，同时保持源描述符的核心功能。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：通过包装现有{@link PropertyDescriptor}实例扩展功能</li>
 *   <li>函数式支持：作为函数式接口可通过lambda表达式创建实例</li>
 *   <li>行为委托：默认方法将操作转发给被包装的源描述符</li>
 *   <li>不可变性保证：包装器应保持源描述符的不可变特性</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的源属性描述符类型，需实现{@link PropertyDescriptor}</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>属性名称转换（如驼峰式与下划线式互转）</li>
 *   <li>访问权限控制（临时禁用属性读写）</li>
 *   <li>元数据增强（添加额外的属性描述信息）</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see PropertyDescriptor
 * @see AccessibleDescriptorWrapper
 * @see NamedAccessibleDescriptor
 */
@FunctionalInterface
public interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
        extends PropertyDescriptor, AccessibleDescriptorWrapper<W> {
    
    /**
     * 获取属性名称（默认委托给源描述符）
     * <p>
     * 该默认实现从被包装的源描述符获取属性名称，
     * 子类可覆盖此方法实现自定义名称逻辑。
     * </p>
     * 
     * @return 属性名称
     */
    @Override
    default String getName() {
        return getSource().getName();
    }

    /**
     * 创建重命名的属性描述符（默认委托给源描述符）
     * <p>
     * 该默认实现调用被包装源描述符的{@link PropertyDescriptor#rename(String)}方法，
     * 子类可覆盖此方法自定义重命名逻辑。
     * </p>
     * 
     * @param name 新属性名称
     * @return 重命名的属性描述符
     */
    @Override
    default PropertyDescriptor rename(String name) {
        return getSource().rename(name);
    }
}