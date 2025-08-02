package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.transform.templates.ArrayTemplate;

/**
 * 数组形式的属性模板实现，继承自{@link ArrayTemplate}并实现{@link PropertyTemplateWrapper}，
 * 提供以数组结构访问属性描述符的能力，支持键唯一性约束和模板包装功能。
 * <p>
 * 该类采用装饰器模式包装源属性模板，将属性描述符组织为数组结构，
 * 适用于需要保持属性顺序或通过索引快速访问属性的场景，如批量属性操作、数据绑定等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>数组结构存储：将属性描述符按顺序组织为数组结构</li>
 *   <li>唯一性约束：支持通过构造参数设置键的唯一性（{@code uniqueness}）</li>
 *   <li>装饰器模式：包装源属性模板，保持原有功能的同时提供数组访问方式</li>
 *   <li>实例复用：{@link #asArray(boolean)}方法会复用实例，避免重复创建</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code T}：属性描述符类型，需实现{@link PropertyDescriptor}</li>
 *   <li>{@code W}：被包装的源属性模板类型，需实现{@link PropertyTemplate}</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyTemplate
 * @see PropertyTemplateWrapper
 * @see ArrayTemplate
 */
public class ArrayPropertyTemplate<T extends PropertyDescriptor, W extends PropertyTemplate<T>>
        extends ArrayTemplate<T, W> implements PropertyTemplateWrapper<T, W> {

    /**
     * 构造数组形式的属性模板
     * 
     * @param source 被包装的源属性模板，不可为null
     * @param uniqueness 是否要求键唯一，true表示键不可重复
     */
    public ArrayPropertyTemplate(W source, boolean uniqueness) {
        super(source, uniqueness);
    }

    /**
     * 获取数组形式的属性模板（支持唯一性约束）
     * <p>
     * 该方法会根据唯一性参数决定是否复用当前实例：
     * <ul>
     *   <li>若当前唯一性设置与参数一致，直接返回自身</li>
     *   <li>否则调用源模板的{@link PropertyTemplate#asArray(boolean)}方法</li>
     * </ul>
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的属性模板实例
     */
    @Override
    public PropertyTemplate<T> asArray(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
    }
}