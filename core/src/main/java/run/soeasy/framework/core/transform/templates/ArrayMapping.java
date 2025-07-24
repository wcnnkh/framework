package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.ArrayDictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 数组形式的映射包装器，继承自{@link ArrayDictionary}并实现{@link MappingWrapper}接口，
 * 用于将映射转换为数组形式的表示，并可选择强制键的唯一性。
 * <p>
 * 该类通过包装源映射实例，将其转换为基于数组的实现，保持元素的顺序性。
 * 当启用唯一性约束时，插入重复键将覆盖原有值；否则允许存在重复键。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>数组结构：基于数组实现，保持元素的插入顺序</li>
 *   <li>唯一性约束：通过构造参数控制键的唯一性</li>
 *   <li>包装器模式：保留对源映射的引用，支持链式操作</li>
 *   <li>集合转换：可根据需要转换回其他集合形式</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>访问性能：基于数组实现，键查找时间复杂度为O(n)</li>
 *   <li>插入/删除开销：数组插入/删除操作需要移动元素，性能较低</li>
 *   <li>线程安全：未实现线程安全机制，多线程环境下需外部同步</li>
 *   <li>空值处理：允许存储null值，可能导致空指针异常</li>
 * </ul>
 * </p>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，必须实现{@link TypedValueAccessor}接口
 * @param <W> 源映射的类型，需实现{@link Mapping<K, V>}
 * 
 * @author soeasy.run
 * @see ArrayDictionary
 * @see MappingWrapper
 * @see TypedValueAccessor
 */
public class ArrayMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
        extends ArrayDictionary<K, V, KeyValue<K, V>, W> implements MappingWrapper<K, V, W> {

    /**
     * 构造一个数组形式的映射包装器
     * 
     * @param source 源映射实例，不可为null
     * @param uniqueMapping 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @throws NullPointerException 若源映射为null
     */
    public ArrayMapping(W source, boolean uniqueMapping) {
        super(source, uniqueMapping);
    }

    /**
     * 将映射转换为数组形式
     * <p>
     * 若当前实例已满足唯一性要求（根据构造时的参数），则返回自身；
     * 否则委托给源映射创建新的数组形式实例。
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的映射实例
     */
    @Override
    public Mapping<K, V> asArray(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
    }

    /**
     * 将映射转换为Map形式
     * <p>
     * 委托给源映射创建Map形式的实例。
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的映射实例
     */
    @Override
    public Mapping<K, V> asMap(boolean uniqueness) {
        return getSource().asMap(uniqueness);
    }

    /**
     * 重写toString方法，提供更友好的字符串表示
     * 
     * @return 包含映射元素信息的字符串
     */
    @Override
    public String toString() {
        return "ArrayMapping{" +
                "size=" + size() +
                ", uniqueness=" + isUniqueness() +
                ", source=" + (getSource() != null ? getSource().getClass().getSimpleName() : "null") +
                '}';
    }
}