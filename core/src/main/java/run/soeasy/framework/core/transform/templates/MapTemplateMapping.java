package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * Map形式的模板映射包装器，继承自{@link MapTemplate}并实现{@link TemplateMappingWrapper}接口，
 * 用于将模板映射转换为Map形式的表示，并可选择强制键的唯一性。
 * <p>
 * 该类通过包装源模板映射实例，将其转换为基于Map的实现，提供快速的键值访问能力。
 * 当启用唯一性约束时，插入重复键将覆盖原有值；否则允许存在重复键。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>Map结构：基于Map实现，提供O(1)时间复杂度的键值访问</li>
 *   <li>唯一性约束：通过构造参数控制键的唯一性</li>
 *   <li>包装器模式：保留对源模板映射的引用，支持链式操作</li>
 *   <li>集合转换：可根据需要转换回其他集合形式</li>
 * </ul>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>顺序丢失：Map不保证元素顺序，可能导致与源模板映射顺序不一致</li>
 *   <li>哈希冲突：若键的哈希函数不合理，可能影响性能</li>
 *   <li>线程安全：未实现线程安全机制，多线程环境下需外部同步</li>
 *   <li>空值处理：允许存储null值，可能导致空指针异常</li>
 * </ul>
 *
 * @param <E> 模板映射元素的类型，必须实现{@link TypedValueAccessor}接口
 * @param <W> 源模板映射的类型，需实现{@link TemplateMapping}
 * 
 * @author soeasy.run
 * @see MapTemplate
 * @see TemplateMappingWrapper
 * @see TypedValueAccessor
 */
public class MapTemplateMapping<E extends TypedValueAccessor, W extends TemplateMapping<E>> extends MapTemplate<E, W>
        implements TemplateMappingWrapper<E, W> {

    /**
     * 构造一个Map形式的模板映射包装器
     * 
     * @param source 源模板映射实例，不可为null
     * @param uniqueness 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @throws NullPointerException 若源模板映射为null
     */
    public MapTemplateMapping(W source, boolean uniqueness) {
        super(source, uniqueness);
    }

    /**
     * 将模板映射转换为Map形式
     * <p>
     * 若当前实例已满足唯一性要求（根据构造时的参数），则返回自身；
     * 否则委托给源模板映射创建新的Map形式实例。
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的模板映射实例
     */
    @Override
    public TemplateMapping<E> asMap(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
    }

    /**
     * 将模板映射转换为数组形式
     * <p>
     * 委托给源模板映射创建数组形式的实例。
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的模板映射实例
     */
    @Override
    public TemplateMapping<E> asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }

    /**
     * 重写toString方法，提供更友好的字符串表示
     * 
     * @return 包含模板映射元素信息的字符串
     */
    @Override
    public String toString() {
        return "MapTemplateMapping{" +
                "size=" + size() +
                ", uniqueness=" + isUniqueness() +
                ", source=" + (getSource() != null ? getSource().getClass().getSimpleName() : "null") +
                '}';
    }
}