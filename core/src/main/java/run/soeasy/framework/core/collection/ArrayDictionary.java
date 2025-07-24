package run.soeasy.framework.core.collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 数组形式的字典实现，将源字典转换为有序的数组视图。
 * 该类实现了Dictionary接口，提供基于数组的键值对存储和访问方式，
 * 支持键唯一性约束和线程安全的缓存机制。
 *
 * <p>核心特性：
 * <ul>
 *   <li>将源字典包装为有序数组视图，保留元素插入顺序</li>
 *   <li>支持键唯一性约束，在唯一性模式下重复键会抛出异常</li>
 *   <li>使用volatile缓存列表数据，通过synchronized保证线程安全的刷新</li>
 *   <li>提供不可变的列表视图，防止外部修改影响内部状态</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要按插入顺序访问键值对的场景</li>
 *   <li>需要键唯一性约束的数据校验场景</li>
 *   <li>需要将字典转换为有序数组视图的场景</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <E> 键值对元素类型，必须实现KeyValue接口
 * @param <W> 源字典类型，必须实现Dictionary接口
 * @see Dictionary
 * @see KeyValue
 * @see DictionaryWrapper
 */
public class ArrayDictionary<K, V, E extends KeyValue<K, V>, W extends Dictionary<K, V, E>>
        implements Dictionary<K, V, E>, DictionaryWrapper<K, V, E, W> {
    
    /** 被包装的源字典 */
    @NonNull
    @Getter
    private final W source;
    
    /** 缓存的键值对列表，volatile保证可见性 */
    private volatile List<E> list;
    
    /** 是否要求键的唯一性 */
    @Getter
    private final boolean uniqueness;

    /**
     * 创建数组字典实例。
     * 
     * @param source    被包装的源字典，不可为null
     * @param uniqueness 是否要求键的唯一性
     */
    public ArrayDictionary(@NonNull W source, boolean uniqueness) {
        this.source = source;
        this.uniqueness = uniqueness;
    }

    /**
     * 重新加载键值对列表，支持强制刷新。
     * 该方法使用双重检查锁定模式确保线程安全，仅在必要时重新加载数据。
     * 
     * @param force 是否强制刷新（true表示忽略当前缓存，强制重新加载）
     * @return 若成功重新加载返回true，否则返回false
     */
    public boolean reload(boolean force) {
        if (force || this.list == null) {
            synchronized (this) {
                if (force || this.list == null) {
                    List<E> list;
                    if (this.uniqueness) {
                        // 唯一性模式：使用LinkedHashMap确保键唯一
                        Map<K, E> map = new LinkedHashMap<>();
                        for (E element : source.getElements()) {
                            if (map.containsKey(element.getKey())) {
                                throw new NoUniqueElementException(String.valueOf(element.getKey()));
                            }
                            map.put(element.getKey(), element);
                        }
                        list = map.entrySet().stream().map((e) -> e.getValue()).collect(Collectors.toList());
                    } else {
                        // 非唯一性模式：直接转换为列表
                        list = source.getElements().toList();
                    }
                    // 转换为不可变列表
                    this.list = CollectionUtils.newReadOnlyList(list);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将字典转换为数组形式。
     * 由于当前实例已为数组形式，若唯一性设置相同则返回自身，否则委托给源字典。
     * 
     * @param uniqueness 是否要求键的唯一性
     * @return 数组形式的字典实例
     */
    @Override
    public Dictionary<K, V, E> asArray(boolean uniqueness) {
        return this.uniqueness == uniqueness ? this : getSource().asArray(uniqueness);
    }

    /**
     * 判断字典是否以数组形式组织。
     * 
     * @return 始终返回true，因为当前实例为数组形式
     */
    @Override
    public boolean isArray() {
        return true;
    }

    /**
     * 判断字典是否以Map形式组织。
     * 
     * @return 始终返回false，因为当前实例为数组形式
     */
    @Override
    public boolean isMap() {
        return false;
    }

    /**
     * 获取键值对列表，若未加载则触发懒加载。
     * 
     * @return 不可变的键值对列表
     */
    public List<E> getList() {
        reload(false);
        return list;
    }

    /**
     * 返回键值对数量。
     * 
     * @return 键值对数量
     */
    @Override
    public int size() {
        return getList().size();
    }

    /**
     * 判断是否包含键值对。
     * 
     * @return 若包含键值对返回true，否则返回false
     */
    @Override
    public boolean hasElements() {
        return !getList().isEmpty();
    }

    /**
     * 获取所有键的集合。
     * 
     * @return 包含所有键的元素集合
     */
    @Override
    public Elements<K> keys() {
        return Elements.of(getList()).map((e) -> e.getKey());
    }
}