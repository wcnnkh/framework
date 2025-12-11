package run.soeasy.framework.core.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于LinkedHashMap实现的LRU（最近最少使用）缓存Map。
 * 当元素数量超过最大容量时，自动移除最久未使用的元素，
 * 适用于需要控制缓存大小并保持热点数据的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承LinkedHashMap的访问顺序特性（accessOrder=true）</li>
 *   <li>当元素数量超过maxCapacity时，自动移除最久未使用的元素</li>
 *   <li>线程不安全：建议在单线程环境使用或外部同步</li>
 *   <li>支持自定义初始容量和负载因子</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建容量为100的LRU缓存
 * LRULinkedHashMap<String, Object> cache = new LRULinkedHashMap<>(100);
 * 
 * // 添加元素
 * cache.put("key1", "value1");
 * cache.put("key2", "value2");
 * 
 * // 访问元素会更新其访问顺序
 * cache.get("key1");
 * 
 * // 当元素数量超过100时，最久未使用的元素将被自动移除
 * }</pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see LinkedHashMap
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> implements MapRegistry<K, V>{
    private static final long serialVersionUID = 1L;
    private final int maxCapacity;

    /**
     * 创建具有指定最大容量的LRU缓存Map。
     * 初始容量为maxCapacity+1，负载因子为1，访问顺序为true。
     *
     * @param maxCapacity 最大容量，必须大于0
     */
    public LRULinkedHashMap(int maxCapacity) {
        this(maxCapacity, maxCapacity + 1, 1);
    }

    /**
     * 创建具有指定参数的LRU缓存Map。
     * 访问顺序默认为true，以实现LRU机制。
     *
     * @param maxCapacity    最大容量，必须大于0
     * @param initialCapacity 初始容量
     * @param loadFactor      负载因子
     */
    public LRULinkedHashMap(int maxCapacity, int initialCapacity, float loadFactor) {
        this(maxCapacity, initialCapacity, loadFactor, true);
    }

    /**
     * 创建具有指定参数的LRU缓存Map。
     * 注意：若accessOrder设为false，则不具备LRU特性，退化为普通LinkedHashMap。
     *
     * @param maxCapacity    最大容量，必须大于0
     * @param initialCapacity 初始容量
     * @param loadFactor      负载因子
     * @param accessOrder     是否启用访问顺序（true为启用，false为插入顺序）
     */
    public LRULinkedHashMap(int maxCapacity, int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxCapacity = maxCapacity;
    }

    /**
     * 返回缓存的最大容量。
     *
     * @return 最大容量
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * 判断是否应移除最旧的元素。
     * 当Map大小超过最大容量时返回true，触发LinkedHashMap自动移除最旧元素。
     *
     * @param eldest 最旧的元素
     * @return 当Map大小超过最大容量时返回true，否则返回false
     */
    @Override
    protected final boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > getMaxCapacity();
    }
}