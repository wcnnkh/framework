package run.soeasy.framework.core.transform.property;

/**
 * 数组形式的属性映射实现，继承自{@link ArrayPropertyTemplate}并实现{@link PropertyMappingWrapper}，
 * 提供以数组结构访问属性映射的能力，支持键唯一性约束和映射包装功能。
 * <p>
 * 该类采用装饰器模式包装源属性映射，将属性访问器组织为数组结构，
 * 适用于需要保持属性顺序或通过索引快速访问属性的场景，如批量属性操作、数据绑定等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>数组结构存储：将属性访问器按顺序组织为数组结构</li>
 *   <li>唯一性约束：支持通过构造参数设置键的唯一性（{@code uniqueness}）</li>
 *   <li>装饰器模式：包装源属性映射，保持原有功能的同时提供数组访问方式</li>
 *   <li>实例复用：{@link #asArray(boolean)}方法会复用实例，避免重复创建</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code V}：属性访问器类型，需实现{@link PropertyAccessor}</li>
 *   <li>{@code W}：被包装的源属性映射类型，需实现{@link PropertyMapping<V>}</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see PropertyMappingWrapper
 * @see ArrayPropertyTemplate
 */
public class ArrayPropertyMapping<V extends PropertyAccessor, W extends PropertyMapping<V>>
        extends ArrayPropertyTemplate<V, W> implements PropertyMappingWrapper<V, W> {

    /**
     * 构造数组形式的属性映射
     * 
     * @param source 被包装的源属性映射，不可为null
     * @param uniqueness 是否要求键唯一，true表示键不可重复
     */
    public ArrayPropertyMapping(W source, boolean uniqueness) {
        super(source, uniqueness);
    }

    /**
     * 获取数组形式的属性映射（支持唯一性约束）
     * <p>
     * 该方法会根据唯一性参数决定是否复用当前实例：
     * <ul>
     *   <li>若当前唯一性设置与参数一致，直接返回自身</li>
     *   <li>否则调用源映射的{@link PropertyMapping#asArray(boolean)}方法</li>
     * </ul>
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的属性映射实例
     */
    @Override
    public PropertyMapping<V> asArray(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
    }
}