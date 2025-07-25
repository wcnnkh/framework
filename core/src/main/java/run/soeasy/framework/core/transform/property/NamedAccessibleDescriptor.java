package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.convert.value.AccessibleDescriptorWrapper;
import run.soeasy.framework.core.domain.Wrapped;

/**
 * 带名称的可访问描述符，继承自{@link Wrapped}并实现{@link PropertyDescriptor}和{@link AccessibleDescriptorWrapper}接口，
 * 用于为{@link AccessibleDescriptor}添加名称属性，同时保留对原始描述符的访问能力，是属性转换场景中连接名称与类型信息的桥梁。
 * 
 * <p>核心功能：
 * - 封装{@link AccessibleDescriptor}实例，通过{@link Wrapped}提供对原始描述符的访问；
 * - 实现{@link PropertyDescriptor}接口，提供属性名称的获取与重命名能力；
 * - 作为{@link AccessibleDescriptorWrapper}，暴露原始描述符的类型信息（如字段类型、方法参数类型等）。
 * 
 * @param <W> 被封装的{@link AccessibleDescriptor}类型，需实现可访问描述符接口
 * @author soeasy.run
 * @see Wrapped
 * @see PropertyDescriptor
 * @see AccessibleDescriptor
 * @see AccessibleDescriptorWrapper
 */
public class NamedAccessibleDescriptor<W extends AccessibleDescriptor> extends Wrapped<W>
		implements PropertyDescriptor, AccessibleDescriptorWrapper<W> {

    /**
     * 当前属性的名称（非空），用于标识属性（如JavaBean的字段名、Map的键名等）
     */
    private final String name;

    /**
     * 初始化带名称的可访问描述符，关联原始描述符与属性名称
     * 
     * @param source 被封装的可访问描述符（非空，提供类型、访问方式等基础信息）
     * @param name 属性名称（非空，用于标识当前属性）
     */
    public NamedAccessibleDescriptor(W source, String name) {
        super(source);
        this.name = name;
    }

    /**
     * 获取当前属性的名称
     * 
     * @return 属性名称（非空字符串）
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 创建一个新的{@link NamedAccessibleDescriptor}实例，保留原始描述符但使用新名称
     * 
     * <p>此方法用于属性重命名场景（如转换过程中需要修改属性名称），新实例与当前实例共享原始描述符，
     * 仅属性名称不同，避免重复创建描述符对象。
     * 
     * @param name 新的属性名称（非空）
     * @return 新的带名称描述符实例（非空，原始描述符不变，名称更新）
     */
    @Override
    public PropertyDescriptor rename(String name) {
        return new NamedAccessibleDescriptor<>(getSource(), name);
    }
}