package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Set集合包装器接口，用于对原生Set集合进行功能增强和操作代理。
 * 该接口继承自Java的Set接口和CollectionWrapper接口，
 * 提供了对底层Set集合的透明包装，并允许添加额外功能。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过委托模式实现对原生Set的功能增强</li>
 *   <li>所有方法默认委派给被包装的源Set实现</li>
 *   <li>支持Java 8的Stream API和函数式操作</li>
 *   <li>保持与原生Set完全一致的行为和语义</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对现有Set添加额外功能（如日志记录、权限控制）</li>
 *   <li>需要统一处理不同类型的Set实现</li>
 *   <li>需要在不修改原始Set的情况下添加自定义行为</li>
 * </ul>
 *
 * @param <E> 集合元素类型
 * @param <W> 被包装的Set类型，必须实现Set接口
 * @see Set
 * @see CollectionWrapper
 */
public interface SetWrapper<E, W extends Set<E>> extends Set<E>, CollectionWrapper<E, W> {

    /**
     * 将指定元素添加到此集合中（如果尚未存在）。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param e 要添加的元素
     * @return 如果此集合尚未包含指定元素，则返回true
     */
    @Override
    default boolean add(E e) {
        return getSource().add(e);
    }

    /**
     * 将指定集合中的所有元素添加到此集合中（如果尚未存在）。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param c 包含要添加到此集合的元素的集合
     * @return 如果此集合因调用而更改，则返回true
     */
    @Override
    default boolean addAll(Collection<? extends E> c) {
        return getSource().addAll(c);
    }

    /**
     * 从此集合中移除所有元素。
     * 该方法默认委派给被包装的源Set实现。
     */
    @Override
    default void clear() {
        getSource().clear();
    }

    /**
     * 如果此集合包含指定元素，则返回true。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param o 要检查是否存在于此集合中的元素
     * @return 如果此集合包含指定元素，则返回true
     */
    @Override
    default boolean contains(Object o) {
        return getSource().contains(o);
    }

    /**
     * 如果此集合包含指定集合中的所有元素，则返回true。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param c 要检查是否存在于此集合中的集合
     * @return 如果此集合包含指定集合中的所有元素，则返回true
     */
    @Override
    default boolean containsAll(Collection<?> c) {
        return getSource().containsAll(c);
    }

    /**
     * 对集合中的每个元素执行给定操作，直到所有元素都被处理或操作抛出异常。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param action 要对每个元素执行的操作
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        getSource().forEach(action);
    }

    /**
     * 如果此集合不包含任何元素，则返回true。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 如果此集合不包含任何元素，则返回true
     */
    @Override
    default boolean isEmpty() {
        return getSource().isEmpty();
    }

    /**
     * 返回此集合中元素的迭代器。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 此集合中元素的迭代器
     */
    @Override
    default Iterator<E> iterator() {
        return getSource().iterator();
    }

    /**
     * 返回可能并行的Stream流。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 可能并行的Stream流
     */
    @Override
    default Stream<E> parallelStream() {
        return getSource().parallelStream();
    }

    /**
     * 如果指定元素存在，则从此集合中移除该元素。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param o 要从此集合中移除的元素（如果存在）
     * @return 如果此集合包含指定元素，则返回true
     */
    @Override
    default boolean remove(Object o) {
        return getSource().remove(o);
    }

    /**
     * 从此集合中移除指定集合中包含的所有元素。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param c 包含要从此集合中移除的元素的集合
     * @return 如果此集合因调用而更改，则返回true
     */
    @Override
    default boolean removeAll(Collection<?> c) {
        return getSource().removeAll(c);
    }

    /**
     * 移除满足给定谓词的此集合的所有元素。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param filter 一个谓词，如果元素应被移除则返回true
     * @return 如果任何元素被移除则返回true
     */
    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        return getSource().removeIf(filter);
    }

    /**
     * 仅保留此集合中包含在指定集合中的元素。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param c 包含要保留在此集合中的元素的集合
     * @return 如果此集合因调用而更改，则返回true
     */
    @Override
    default boolean retainAll(Collection<?> c) {
        return getSource().retainAll(c);
    }

    /**
     * 返回此集合中的元素数。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 此集合中的元素数
     */
    @Override
    default int size() {
        return getSource().size();
    }

    /**
     * 创建一个Spliterator遍历此集合中的元素。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 用于遍历此集合中元素的Spliterator
     */
    @Override
    default Spliterator<E> spliterator() {
        return getSource().spliterator();
    }

    /**
     * 返回以此集合为源的顺序Stream流。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 顺序Stream流
     */
    @Override
    default Stream<E> stream() {
        return getSource().stream();
    }

    /**
     * 返回包含此集合中所有元素的数组。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @return 包含此集合中所有元素的数组
     */
    @Override
    default Object[] toArray() {
        return getSource().toArray();
    }

    /**
     * 返回包含此集合中所有元素的数组；返回数组的运行时类型是指定数组的运行时类型。
     * 该方法默认委派给被包装的源Set实现。
     *
     * @param <T> 包含集合的数组的组件类型
     * @param a 要存储此集合元素的数组（如果足够大）；否则，将为此分配一个相同运行时类型的新数组
     * @return 包含此集合元素的数组
     */
    @Override
    default <T> T[] toArray(T[] a) {
        return getSource().toArray(a);
    }
}