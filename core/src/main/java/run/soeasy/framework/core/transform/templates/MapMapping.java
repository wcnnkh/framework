package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.MapDictionary;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * Map形式的映射包装器，继承自{@link MapDictionary}并实现{@link MappingWrapper}接口，
 * 用于将映射转换为Map形式的表示，并可选择强制键的唯一性和保持元素顺序。
 * <p>
 * 该类通过包装源映射实例，将其转换为基于Map的实现，提供快速的键值访问能力。
 * 当启用唯一性约束时，插入重复键将覆盖原有值；否则允许存在重复键。
 * 当启用顺序约束时，使用LinkedHashMap保持元素插入顺序。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>Map结构：基于Map实现，提供O(1)时间复杂度的键值访问</li>
 *   <li>唯一性约束：通过构造参数控制键的唯一性</li>
 *   <li>顺序保持：通过构造参数控制是否保持元素插入顺序</li>
 *   <li>包装器模式：保留对源映射的引用，支持链式操作</li>
 *   <li>集合转换：可根据需要转换回其他集合形式</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>顺序与性能：启用顺序保持会增加内存开销，降低插入/删除性能</li>
 *   <li>哈希冲突：若键的哈希函数不合理，可能影响性能</li>
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
 * @see MapDictionary
 * @see MappingWrapper
 * @see TypedValueAccessor
 */
public class MapMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
        extends MapDictionary<K, V, KeyValue<K, V>, W> implements MappingWrapper<K, V, W> {

    /**
     * 构造一个Map形式的映射包装器
     * 
     * @param source 源映射实例，不可为null
     * @param orderly 是否保持元素插入顺序，true表示保持顺序，false表示不保持
     * @param uniqueMapping 是否要求键唯一，true表示键不可重复，false表示允许重复键
     * @throws NullPointerException 若源映射为null
     */
    public MapMapping(W source, boolean orderly, boolean uniqueMapping) {
        super(source, orderly, uniqueMapping);
    }

    /**
     * 将映射转换为Map形式
     * <p>
     * 若当前实例已满足唯一性要求（根据构造时的参数），则返回自身；
     * 否则委托给源映射创建新的Map形式实例。
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map形式的映射实例
     */
    @Override
    public Mapping<K, V> asMap(boolean uniqueness) {
        return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
    }

    /**
     * 将映射转换为数组形式
     * <p>
     * 委托给源映射创建数组形式的实例。
     * </p>
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组形式的映射实例
     */
    @Override
    public Mapping<K, V> asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }
}