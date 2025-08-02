package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 集合元素包装器接口，用于将标准Java Set转换为支持框架内Elements操作的接口。
 * 该接口继承自SetWrapper和CollectionElementsWrapper，
 * 提供了集合操作与元素流操作的统一抽象，特别优化了集合去重特性的处理。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过继承SetWrapper和CollectionElementsWrapper实现双重特性</li>
 *   <li>默认实现保持与原生Set一致的行为和语义</li>
 *   <li>distinct()和toSet()方法直接返回自身，优化集合操作</li>
 *   <li>所有方法默认委托给被包装的源Set实现</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要在保持Set特性的同时使用框架内Elements API</li>
 *   <li>需要统一处理不同类型的Set实现</li>
 *   <li>需要在集合操作中应用流式处理和转换</li>
 * </ul>
 *
 * @param <E> 集合元素类型
 * @param <W> 被包装的Set类型，必须实现Set接口
 * @see SetWrapper
 * @see CollectionElementsWrapper
 * @see Elements
 */
public interface SetElementsWrapper<E, W extends Set<E>>
        extends SetWrapper<E, W>, CollectionElementsWrapper<E, W> {

    /**
     * 判断集合是否包含指定元素。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @param o 要检查的元素
     * @return 如果包含返回true，否则返回false
     */
    @Override
    default boolean contains(Object o) {
        return SetWrapper.super.contains(o);
    }

    /**
     * 返回去重后的元素集合。
     * 由于Set本身保证元素唯一性，该方法直接返回当前实例。
     *
     * @return 当前实例
     */
    @Override
    default Elements<E> distinct() {
        return this;
    }

    /**
     * 对集合中的每个元素执行指定操作。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @param action 要执行的操作
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        SetWrapper.super.forEach(action);
    }

    /**
     * 判断集合是否为空。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @return 如果集合为空返回true，否则返回false
     */
    @Override
    default boolean isEmpty() {
        return SetWrapper.super.isEmpty();
    }

    /**
     * 返回集合的迭代器。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @return 集合的迭代器
     */
    @Override
    default Iterator<E> iterator() {
        return SetWrapper.super.iterator();
    }

    /**
     * 返回集合的顺序流。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @return 集合的顺序流
     */
    @Override
    default Stream<E> stream() {
        return SetWrapper.super.stream();
    }

    /**
     * 将集合转换为数组。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @return 包含集合所有元素的数组
     */
    @Override
    default Object[] toArray() {
        return SetWrapper.super.toArray();
    }

    /**
     * 将集合转换为指定类型的数组。
     * 该方法默认委托给SetWrapper接口的实现。
     *
     * @param <T> 数组元素类型
     * @param a   目标数组
     * @return 包含集合所有元素的数组
     */
    @Override
    default <T> T[] toArray(T[] a) {
        return SetWrapper.super.toArray(a);
    }

    /**
     * 将当前集合转换为SetElementsWrapper。
     * 由于当前接口已经实现了Set特性，该方法直接返回当前实例。
     *
     * @return 当前实例
     */
    @Override
    default SetElementsWrapper<E, W> toSet() {
        return this;
    }
}