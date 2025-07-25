package run.soeasy.framework.core.exchange.container.map;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.collection.CollectionContainer;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

/**
 * 基于TreeSet的有序集合容器实现，支持元素按自然顺序或自定义比较器排序。
 * <p>
 * 该容器继承自{@link CollectionContainer}，使用TreeSet作为底层存储结构，
 * 确保元素按指定顺序排列，并支持动态修改比较器以重新排序。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>有序集合：元素按自然顺序或指定比较器维护顺序</li>
 *   <li>动态排序：支持运行时修改比较器并触发重排序</li>
 *   <li>线程安全操作：修改比较器时使用写锁保证原子性</li>
 *   <li>事件通知：重排序时发布更新事件</li>
 *   <li>唯一性保证：基于TreeSet特性，元素不能重复</li>
 * </ul>
 *
 * @param <E> 元素类型，必须实现Comparable接口或提供自定义比较器
 * 
 * @author soeasy.run
 * @see CollectionContainer
 * @see TreeSet
 */
public class TreeSetContainer<E> extends CollectionContainer<E, TreeSet<ElementRegistration<E>>> {
    
    /** 用于元素排序的比较器 */
    private volatile Comparator<? super E> comparator;

    /**
     * 默认构造函数，使用元素的自然顺序排序
     * <p>
     * 要求元素类型E必须实现Comparable接口，否则在插入元素时会抛出ClassCastException。
     */
    @SuppressWarnings("unchecked")
    public TreeSetContainer() {
        super(() -> {
            return new TreeSet<>((a, b) -> {
                // 使用元素的自然顺序比较
                Comparable<Object> comparable = (Comparable<Object>) a.getPayload();
                return comparable.compareTo(b.getPayload());
            });
        });
    }

    /**
     * 获取当前使用的比较器
     * 
     * @return 当前比较器，若使用自然顺序则返回null
     */
    public Comparator<? super E> getComparator() {
        return comparator;
    }

    /**
     * 创建新的TreeSet实例
     * <p>
     * 如果已设置比较器，则使用该比较器创建TreeSet；否则使用默认比较器。
     * 
     * @return 新的TreeSet实例
     */
    @Override
    protected TreeSet<ElementRegistration<E>> newContainer() {
        if (comparator != null) {
            return new TreeSet<>(Comparator.comparing(PayloadRegistration::getPayload, comparator));
        }
        return super.newContainer();
    }

    /**
     * 设置新的比较器并触发容器重排序
     * <p>
     * 该操作会获取写锁以确保原子性，将所有元素转移到使用新比较器的TreeSet中，
     * 并发布所有元素的更新事件。
     * 
     * @param comparator 新的比较器，可为null以使用元素的自然顺序
     */
    public void setComparator(Comparator<? super E> comparator) {
        if (comparator == this.comparator) {
            return;
        }

        Lock lock = writeLock();
        lock.lock();
        try {
            update((set) -> {
                this.comparator = comparator;
                if (set == null) {
                    return false;
                }

                reset();
                TreeSet<ElementRegistration<E>> container = newContainer();
                container.addAll(set);
                set.clear();
                set.addAll(container);
                List<ChangeEvent<E>> events = container.stream()
                        .map((e) -> new ChangeEvent<>(e.getPayload(), ChangeType.UPDATE)).collect(Collectors.toList());
                getPublisher().publish(Elements.of(events));
                return true;
            });
        } finally {
            lock.unlock();
        }
    }
}