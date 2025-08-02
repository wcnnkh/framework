package run.soeasy.framework.core.transform.property;

import lombok.NonNull;

/**
 * 对象属性集合，继承自{@link PropertyTemplateProperties}并实现{@link TypedProperties}，
 * 用于将对象的属性描述符转换为可访问的属性访问器集合，支持类型化属性操作。
 * <p>
 * 该类通过包装属性模板和目标对象，将模板中的每个属性描述符映射为
 * 针对特定目标对象的属性访问器（{@link ObjectPropertyAccessor}），
 * 从而实现对对象属性的统一访问和操作。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>对象绑定：绑定目标对象，使属性访问器能够读取和修改该对象的属性值</li>
 *   <li>类型安全：实现{@link TypedProperties}接口，支持类型化属性访问</li>
 *   <li>延迟计算：属性值在访问时动态获取，保证与目标对象状态的实时一致性</li>
 *   <li>统一接口：通过属性访问器提供一致的属性读写方式，屏蔽底层实现差异</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code S}：属性类型，需实现{@link Property}</li>
 *   <li>{@code T}：属性模板类型，需实现{@link PropertyTemplate}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>对象属性的批量操作（如属性值复制、校验）</li>
 *   <li>动态属性访问（如基于配置的属性读取/修改）</li>
 *   <li>数据绑定（如将表单数据绑定到对象属性）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyTemplateProperties
 * @see TypedProperties
 * @see ObjectPropertyAccessor
 */
public class ObjectProperties<S extends Property, T extends PropertyTemplate<S>>
        extends PropertyTemplateProperties<S, T, PropertyAccessor> implements TypedProperties {

    /**
     * 构造对象属性集合
     * 
     * @param template 被包装的属性模板，定义属性结构，不可为null
     * @param target 目标对象，属性访问器将操作该对象的属性值，不可为null
     */
    public ObjectProperties(@NonNull T template, Object target) {
        super(template, (e) -> new ObjectPropertyAccessor<>(e, target));
    }
}