package run.soeasy.framework.core.transform.property;

/**
 * 类型化属性包装器接口，继承自{@link TypedProperties}和{@link PropertyMappingWrapper}，
 * 采用装饰器模式实现对目标类型化属性的包装，支持在不修改原属性的前提下扩展其功能。
 * <p>
 * 该接口定义了类型化属性包装的标准规范，默认方法将所有操作委托给被包装的源属性，
 * 子类可通过覆盖特定方法修改属性的检索逻辑、结构转换行为或类型化操作规则。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>装饰器模式：包装现有{@link TypedProperties}实例并扩展功能</li>
 *   <li>委托处理：默认操作转发给被包装的源属性，保持原有逻辑</li>
 *   <li>类型安全：通过泛型约束保证包装前后的类型一致性</li>
 *   <li>多层扩展：同时继承属性映射包装和类型化属性能力，支持复合扩展</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code W}：被包装的源类型化属性类型，需实现{@link TypedProperties}</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>属性访问的增强（如添加缓存、类型校验）</li>
 *   <li>属性结构的动态调整（如按需过滤属性）</li>
 *   <li>属性操作的拦截（如日志记录、权限控制）</li>
 *   <li>属性类型的转换（如统一类型适配）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypedProperties
 * @see PropertyMappingWrapper
 */
@FunctionalInterface
public interface TypedPropertiesWrapper<W extends TypedProperties>
        extends TypedProperties, PropertyMappingWrapper<PropertyAccessor, W> {
    
    /**
     * 转换为Map形式的类型化属性（委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link TypedProperties#asMap(boolean)}方法，
     * 子类可覆盖此方法自定义Map转换逻辑（如添加额外处理）。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的类型化属性
     */
    @Override
    default TypedProperties asMap(boolean uniqueness) {
        return getSource().asMap(uniqueness);
    }

    /**
     * 转换为数组形式的类型化属性（委托给源属性）
     * <p>
     * 该默认实现调用被包装源属性的{@link TypedProperties#asArray(boolean)}方法，
     * 子类可覆盖此方法自定义数组转换逻辑（如添加索引转换）。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的类型化属性
     */
    @Override
    default TypedProperties asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }
}