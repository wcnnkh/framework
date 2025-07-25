package run.soeasy.framework.core.exchange.container;

import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;

/**
 * 原子性键值对注册实现，提供线程安全的键值对管理能力。
 * <p>
 * 该类继承自{@link AbstractEntryRegistration}，实现了{@link EntryRegistration}接口，
 * 使用{@link AtomicReference}确保值的操作具有原子性和线程安全性，适用于多线程环境下的键值对状态管理。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>线程安全：通过{@link AtomicReference}保证值操作的原子性</li>
 *   <li>不可变键：键在构造后不可修改，确保键的唯一性和安全性</li>
 *   <li>原子值更新：支持原子性更新值并返回旧值</li>
 *   <li>生命周期管理：继承父类的注册生命周期控制能力</li>
 * </ul>
 *
 * @param <K> 键的类型，支持泛型定义
 * @param <V> 值的类型，支持泛型定义
 * 
 * @author soeasy.run
 * @see AbstractEntryRegistration
 * @see EntryRegistration
 * @see AtomicReference
 */
@Getter
public class AtomicEntryRegistration<K, V> extends AbstractEntryRegistration<K, V> implements EntryRegistration<K, V> {
    
    /** 不可变键，构造后无法修改 */
    private final K key;
    
    /** 原子值引用，确保值操作的线程安全性 */
    private final AtomicReference<V> valueReference;

    /**
     * 构造函数，初始化键值对注册
     * <p>
     * 创建时指定键和初始值，键在构造后不可变，值通过原子引用管理。
     * 
     * @param key 键，不可为null（取决于业务场景）
     * @param value 初始值，允许为null
     */
    public AtomicEntryRegistration(K key, V value) {
        this.key = key;
        this.valueReference = new AtomicReference<V>(value);
    }

    /**
     * 获取键
     * <p>
     * 返回构造时指定的键，该键不可变。
     * 
     * @return 键对象
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * 获取当前值
     * <p>
     * 通过原子引用安全获取当前值，支持多线程并发读取。
     * 
     * @return 当前值，可能为null
     */
    @Override
    public V getValue() {
        return valueReference.get();
    }

    /**
     * 原子性更新值并返回旧值
     * <p>
     * 使用原子引用实现值的更新操作，确保多线程环境下的操作原子性。
     * 该操作会受到父类限制器的约束，若注册已被限制则无法更新。
     * 
     * @param value 新值，允许为null
     * @return 旧值，可能为null
     */
    @Override
    public V setValue(V value) {
        return valueReference.getAndSet(value);
    }
}