package run.soeasy.framework.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.collection.factory.MapFactory;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 基于Map实现的字典数据结构，将键值对组织为Map形式并提供缓存和唯一性约束能力。
 * 该类实现了Dictionary接口，支持将键值对集合转换为Map视图，适用于需要快速通过键访问值的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>通过MapFactory动态创建Map实例，支持LinkedHashMap/HashMap等不同实现</li>
 *   <li>支持键的唯一性约束，在唯一性模式下重复键会抛出异常</li>
 *   <li>使用volatile缓存Map实例，通过synchronized保证线程安全的刷新</li>
 *   <li>提供不可变的List值视图，防止外部修改影响内部状态</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要快速通过键查询多值映射的场景（如HTTP参数解析）</li>
 *   <li>需要键唯一性校验的配置数据管理</li>
 *   <li>需要在有序Map和普通Map之间切换的动态字典场景</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <E> 键值对元素类型，必须实现KeyValue接口
 * @param <W> 源字典类型，必须实现Dictionary接口
 * @see Dictionary
 * @see KeyValue
 * @see MapFactory
 */
public class MapDictionary<K, V, E extends KeyValue<K, V>, W extends Dictionary<K, V, E>>
        implements Dictionary<K, V, E>, DictionaryWrapper<K, V, E, W> {
    
    /** 被包装的源字典 */
    @NonNull
    @Getter
    private final W source;
    
    /** 缓存的Map实例，volatile保证可见性 */
    private volatile Map<K, List<V>> map;
    
    /** Map实例创建工厂，支持动态选择Map实现 */
    @NonNull
    private final MapFactory<K, List<V>, Map<K, List<V>>> mapFactory;
    
    /** 是否要求键的唯一性 */
    @Getter
    private final boolean uniqueness;

    /**
     * 创建MapDictionary实例，指定源字典、有序性和唯一性约束。
     * 该构造函数通过MapFactory创建Map实例，有序性决定使用LinkedHashMap或HashMap。
     *
     * @param source    被包装的源字典，不可为null
     * @param orderly   是否保持插入顺序（true使用LinkedHashMap，false使用HashMap）
     * @param uniqueness 是否要求键的唯一性
     */
    public MapDictionary(W source, boolean orderly, boolean uniqueness) {
        this(source, (a, b) -> orderly ? new LinkedHashMap<>(a, b) : new HashMap<>(a, b), uniqueness);
    }

    /**
     * 创建MapDictionary实例，指定源字典、Map工厂和唯一性约束。
     *
     * @param source       被包装的源字典，不可为null
     * @param mapFactory   Map实例创建工厂，不可为null
     * @param uniqueness   是否要求键的唯一性
     */
    public MapDictionary(@NonNull W source, 
                         @NonNull MapFactory<K, List<V>, Map<K, List<V>>> mapFactory, 
                         boolean uniqueness) {
        this.source = source;
        this.mapFactory = mapFactory;
        this.uniqueness = uniqueness;
    }

    /**
     * 重新加载Map数据，支持强制刷新。
     * 该方法使用双重检查锁定模式确保线程安全，仅在必要时重新加载数据。
     * 在唯一性模式下会检查每个键对应的值数量是否为1，否则抛出异常。
     *
     * @param force 是否强制刷新（true表示忽略当前缓存，强制重新加载）
     * @return 若成功重新加载返回true，否则返回false
     */
    public boolean reload(boolean force) {
        if (force || map == null) {
            synchronized (this) {
                if (force || map == null) {
                    Map<K, List<V>> map = mapFactory.create();
                    for (KeyValue<K, V> keyValue : source.getElements()) {
                        List<V> list = map.get(keyValue.getKey());
                        if (list == null) {
                            list = new ArrayList<>(4);
                            map.put(keyValue.getKey(), list);
                        }
                        list.add(keyValue.getValue());
                    }

                    for (Entry<K, List<V>> entry : map.entrySet()) {
                        if (uniqueness) {
                            if (entry.getValue().size() != 1) {
                                throw new NoUniqueElementException(String.valueOf(entry.getKey()));
                            }
                            entry.setValue(Arrays.asList(entry.getValue().get(0)));
                        } else {
                            entry.setValue(CollectionUtils.newReadOnlyList(entry.getValue()));
                        }
                    }
                    this.map = mapFactory.display(map);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将字典转换为Map形式。
     * 由于当前实例已为Map形式，若唯一性设置相同则返回自身，否则委托给源字典。
     *
     * @param uniqueness 是否要求键的唯一性
     * @return Map形式的字典实例
     */
    @Override
    public Dictionary<K, V, E> asMap(boolean uniqueness) {
        return this.uniqueness == uniqueness ? this : getSource().asMap(uniqueness);
    }

    /**
     * 判断字典是否以Map形式组织。
     *
     * @return 始终返回true，因为当前实例为Map形式
     */
    @Override
    public boolean isMap() {
        return true;
    }

    /**
     * 判断字典是否以数组形式组织。
     *
     * @return 始终返回false，因为当前实例为Map形式
     */
    @Override
    public boolean isArray() {
        return false;
    }

    /**
     * 获取缓存的Map实例，若未加载则触发懒加载。
     *
     * @return 不可变的Map实例
     */
    public Map<K, List<V>> getMap() {
        reload(false);
        return map;
    }

    /**
     * 返回键值对数量。
     * 在唯一性模式下返回键的数量，非唯一性模式下返回所有值的数量总和。
     *
     * @return 键值对数量
     */
    @Override
    public int size() {
        return uniqueness ? getMap().size() : getMap().entrySet().stream().mapToInt((e) -> e.getValue().size()).sum();
    }

    /**
     * 判断是否包含键值对。
     *
     * @return 若包含键值对返回true，否则返回false
     */
    @Override
    public boolean hasElements() {
        return !getMap().isEmpty();
    }

    /**
     * 获取指定键对应的所有值的集合。
     *
     * @param key 要查询的键
     * @return 包含该键对应所有值的Elements集合，若键不存在则返回空集合
     */
    @Override
    public Elements<V> getValues(K key) {
        List<V> list = getMap().get(key);
        return list == null ? Elements.empty() : Elements.of(list);
    }

    /**
     * 获取所有键的集合。
     *
     * @return 包含所有键的Elements集合
     */
    @Override
    public Elements<K> keys() {
        return Elements.of(getMap().keySet());
    }

    /**
     * 判断是否包含指定键。
     *
     * @param key 要检查的键
     * @return 若包含指定键返回true，否则返回false
     */
    @Override
    public boolean hasKey(K key) {
        return getMap().containsKey(key);
    }
    
    @Override
    public String toString() {
    	return getMap().toString();
    }
}