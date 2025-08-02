package run.soeasy.framework.core.exchange.container.map;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.container.EntryRegistration;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

/**
 * 基于TreeMap的有序映射容器实现，支持按键的自然顺序或自定义比较器排序。
 * <p>
 * 该容器继承自{@link MapContainer}，使用TreeMap作为底层存储结构，
 * 提供按键排序的映射操作能力，并支持动态修改比较器以重新排序。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>有序映射：按键的自然顺序或指定比较器维护元素顺序</li>
 *   <li>动态排序：支持运行时修改比较器并触发重排序</li>
 *   <li>线程安全操作：修改比较器时使用写锁保证原子性</li>
 *   <li>事件通知：重排序时发布更新事件</li>
 * </ul>
 *
 * @param <K> 键的类型，必须实现Comparable接口或提供自定义比较器
 * @param <V> 值的类型
 * 
 * @author soeasy.run
 * @see MapContainer
 * @see TreeMap
 */
public class TreeMapContainer<K, V> extends MapContainer<K, V, TreeMap<K, EntryRegistration<K, V>>> {
    
    /** 用于键排序的比较器 */
    private Comparator<? super K> comparator;

    /**
     * 默认构造函数，使用键的自然顺序排序
     * <p>
     * 要求键类型K必须实现Comparable接口，否则在插入元素时会抛出ClassCastException。
     */
    public TreeMapContainer() {
        super(TreeMap::new);
    }

    /**
     * 创建新的TreeMap实例
     * <p>
     * 如果已设置比较器，则使用该比较器创建TreeMap；否则使用默认构造函数。
     * 
     * @return 新的TreeMap实例
     */
    @Override
    protected TreeMap<K, EntryRegistration<K, V>> newContainer() {
        if (comparator != null) {
            return new TreeMap<>(comparator);
        }
        return super.newContainer();
    }

    /**
     * 获取当前使用的比较器
     * 
     * @return 当前比较器，若使用自然顺序则返回null
     */
    public Comparator<? super K> getComparator() {
        return comparator;
    }

    /**
     * 设置新的比较器并触发容器重排序
     * <p>
     * 该操作会获取写锁以确保原子性，将所有元素转移到使用新比较器的TreeMap中，
     * 并发布所有元素的更新事件。
     * 
     * @param comparator 新的比较器，可为null以使用键的自然顺序
     */
    public void setComparator(Comparator<? super K> comparator) {
        if (comparator == this.comparator) {
            return;
        }

        Lock lock = writeLock();
        lock.lock();
        try {
            update((map) -> {
                this.comparator = comparator;
                if (map == null) {
                    return false;
                }

                reset();
                TreeMap<K, EntryRegistration<K, V>> container = newContainer();
                container.putAll(map);
                map.clear();
                map.putAll(container);
                List<ChangeEvent<KeyValue<K, V>>> events = container.values().stream()
                        .map((e) -> new ChangeEvent<>((KeyValue<K, V>) e, ChangeType.UPDATE))
                        .collect(Collectors.toList());
                getPublisher().publish(Elements.of(events));
                return true;
            });
        } finally {
            lock.unlock();
        }
    }
}