package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.collection.NoUniqueElementException;
import run.soeasy.framework.core.transform.templates.TemplateMappingWrapper;

/**
 * 属性映射包装器接口，继承自{@link PropertyMapping}、{@link TemplateMappingWrapper}和{@link PropertyTemplateWrapper}，
 * 采用装饰器模式实现对目标属性映射的包装，支持在不修改原映射的前提下扩展其功能。
 * <p>
 * 该接口定义了属性映射包装的标准规范，默认方法将所有操作委托给被包装的源映射，
 * 子类可通过覆盖特定方法修改映射的检索逻辑、结构转换行为或类型转换规则。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装现有{@link PropertyMapping}实例并扩展功能</li>
 *   <li>委托处理：默认操作转发给被包装的源映射，保持原有逻辑</li>
 *   <li>类型安全：通过泛型约束保证包装前后的类型一致性</li>
 *   <li>多层扩展：同时继承模板包装和映射包装能力，支持复合扩展</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code V}：属性访问器类型，需实现{@link PropertyAccessor}</li>
 *   <li>{@code W}：被包装的源属性映射类型，需实现{@link PropertyMapping}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>属性映射的访问控制（如只读映射包装）</li>
 *   <li>映射结果的过滤或增强（如添加默认值）</li>
 *   <li>映射过程的日志记录或监控</li>
 *   <li>动态修改映射结构（如按需调整Map唯一性约束）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see TemplateMappingWrapper
 * @see PropertyTemplateWrapper
 */
public interface PropertyMappingWrapper<V extends PropertyAccessor, W extends PropertyMapping<V>>
        extends PropertyMapping<V>, TemplateMappingWrapper<V, W>, PropertyTemplateWrapper<V, W> {
    
    /**
     * 根据键获取属性访问器（委托给源映射）
     * <p>
     * 该默认实现调用被包装源映射的{@link PropertyMapping#get(Object)}方法，
     * 子类可覆盖此方法实现自定义检索逻辑（如缓存、过滤）。
     * </p>
     * 
     * @param key 检索键，支持数字、字符串或其他类型
     * @return 匹配的属性访问器
     * @throws NoUniqueElementException 当键对应多个属性时抛出
     */
    @Override
    default V get(Object key) throws NoUniqueElementException {
        return getSource().get(key);
    }

    /**
     * 转换为Map形式的属性映射（委托给源映射）
     * <p>
     * 该默认实现调用被包装源映射的{@link PropertyMapping#asMap(boolean)}方法，
     * 子类可覆盖此方法自定义Map映射的创建逻辑（如添加额外处理）。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的属性映射
     */
    @Override
    default PropertyMapping<V> asMap(boolean uniqueness) {
        return getSource().asMap(uniqueness);
    }

    /**
     * 转换为数组形式的属性映射（委托给源映射）
     * <p>
     * 该默认实现调用被包装源映射的{@link PropertyMapping#asArray(boolean)}方法，
     * 子类可覆盖此方法自定义数组映射的创建逻辑（如添加索引转换）。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的属性映射
     */
    @Override
    default PropertyMapping<V> asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }
}