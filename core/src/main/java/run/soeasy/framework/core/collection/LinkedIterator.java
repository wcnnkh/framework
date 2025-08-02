package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.NonNull;

/**
 * 链式迭代器实现，通过函数式接口定义元素迭代逻辑，支持自定义元素遍历规则。
 * 该迭代器通过Predicate判断是否存在下一个元素，通过Function生成下一个元素，
 * 适用于需要自定义迭代逻辑的场景（如链表遍历、生成序列等）。
 *
 * <p>设计特点：
 * <ul>
 *   <li>函数式迭代：通过Predicate和Function定义迭代逻辑，解耦迭代行为</li>
 *   <li>状态保持：内部维护当前元素状态，支持连续迭代</li>
 *   <li>线程安全：hasNext()方法使用synchronized保证多线程环境下的状态一致性</li>
 *   <li>惰性计算：仅在调用next()时获取元素，避免提前计算</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 迭代从1开始的自然数序列，直到超过10
 * LinkedIterator<Integer> iterator = new LinkedIterator<>(
 *     1,
 *     n -> n <= 10,
 *     n -> n + 1
 * );
 *
 * while (iterator.hasNext()) {
 *     System.out.println(iterator.next()); // 输出1到10
 * }
 * }</pre>
 *
 * @param <E> 迭代元素类型
 * @see Iterator
 */
public final class LinkedIterator<E> implements Iterator<E> {

    /** 上一次返回的元素，用于生成下一个元素 */
    private E last;
    
    /** 提供当前元素的供应商，延迟加载元素 */
    private Supplier<E> currentSupplier;
    
    /** 判断是否存在下一个元素的谓词 */
    private final Predicate<? super E> hasNext;
    
    /** 生成下一个元素的函数 */
    private final Function<? super E, ? extends E> next;

    /**
     * 创建链式迭代器实例。
     *
     * @param current   初始元素
     * @param hasNext   判断是否存在下一个元素的谓词
     * @param next      生成下一个元素的函数
     * @throws NullPointerException 如果参数为null
     */
    public LinkedIterator(@NonNull E current, @NonNull Predicate<? super E> hasNext,
            @NonNull Function<? super E, ? extends E> next) {
        this.last = current;
        this.currentSupplier = () -> current;
        this.hasNext = hasNext;
        this.next = next;
    }

    /**
     * 判断是否存在下一个元素。
     * 该方法使用synchronized保证线程安全，会根据last元素和hasNext谓词
     * 计算是否存在下一个元素，并更新currentSupplier。
     *
     * @return 如果存在下一个元素返回true，否则返回false
     */
    @Override
    public synchronized boolean hasNext() {
        if (currentSupplier == null && last != null && hasNext.test(last)) {
            E nextElement = this.next.apply(this.last);
            this.currentSupplier = () -> nextElement;
        }
        return currentSupplier != null;
    }

    /**
     * 获取下一个元素。
     * 该方法先调用hasNext()检查是否存在下一个元素，不存在则抛出异常，
     * 存在则获取元素并清空currentSupplier以确保单次获取。
     *
     * @return 下一个元素
     * @throws NoSuchElementException 如果没有下一个元素
     */
    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        try {
            return this.last = currentSupplier.get();
        } finally {
            this.currentSupplier = null;
        }
    }
}