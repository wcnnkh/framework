package run.soeasy.framework.core.concurrent;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 原子键值对，使用AtomicReference实现值的原子操作。
 * 该类表示一个不可变键与可变值的组合，其中值的操作是原子性的，
 * 适合在多线程环境下安全地更新值。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承AtomicReference，提供值的原子更新能力</li>
 *   <li>键不可变，值可变且操作线程安全</li>
 *   <li>实现Map.Entry和KeyValue接口，无缝集成现有API</li>
 *   <li>支持CAS(Compare-and-Swap)操作，避免锁开销</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>多线程环境下的共享配置项管理</li>
 *   <li>需要原子更新的计数器或状态标识</li>
 *   <li>实现无锁数据结构的基础组件</li>
 *   <li>缓存条目或元数据的线程安全存储</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see AtomicReference
 * @see KeyValue
 * @see Entry
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AtomicEntry<K, V> extends AtomicReference<V> implements KeyValue<K, V>, Entry<K, V> {
    private static final long serialVersionUID = 1L;
    
    /**
     * 不可变的键，在构造时初始化。
     */
    private final K key;
    
    /**
     * 创建具有指定键和初始值的原子键值对。
     *
     * @param key   不可变的键
     * @param value 初始值
     */
    public AtomicEntry(K key, V value) {
        super(value);
        this.key = key;
    }
    
    /**
     * 原子性地设置值并返回旧值。
     * 该方法实现了Map.Entry接口的setValue语义，
     * 与AtomicReference的getAndSet方法行为一致。
     *
     * @param value 新值
     * @return 旧值
     */
    @Override
    public V setValue(V value) {
        return getAndSet(value);
    }

    /**
     * 获取当前值。
     * 该方法实现了Map.Entry接口的getValue语义，
     * 与AtomicReference的get方法行为一致。
     *
     * @return 当前值
     */
    @Override
    public V getValue() {
        return get();
    }
}