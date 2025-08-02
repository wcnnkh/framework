package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 列表元素包装器接口，用于对列表类型的元素集合进行统一封装和操作委托。
 * 该接口继承自ListWrapper和CollectionElementsWrapper，
 * 提供了对底层列表集合的透明包装，所有操作默认委托给被包装的源列表对象。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过包装模式实现对List实例的功能增强和操作代理</li>
 *   <li>所有方法默认委派给被包装的源列表实现</li>
 *   <li>保持与原始列表完全一致的行为和语义</li>
 *   <li>类型安全的泛型设计，确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对现有列表添加额外功能（如索引访问增强、反转操作）</li>
 *   <li>需要统一处理不同类型的列表实现</li>
 *   <li>需要在不修改原始实现的情况下添加自定义行为</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的列表类型，必须实现List接口
 * @see ListWrapper
 * @see CollectionElementsWrapper
 */
public interface ListElementsWrapper<E, W extends List<E>> extends ListWrapper<E, W>, CollectionElementsWrapper<E, W> {

    /**
     * 判断列表是否包含指定元素。
     * 该方法默认委派给被包装的源列表实现。
     *
     * @param o 要检查的元素
     * @return 如果列表包含指定元素返回true，否则返回false
     */
    @Override
    default boolean contains(Object o) {
        return ListWrapper.super.contains(o);
    }

    /**
     * 对列表中的每个元素执行指定操作。
     * 该方法默认委派给被包装的源列表实现。
     *
     * @param action 要对每个元素执行的操作
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        ListWrapper.super.forEach(action);
    }

    /**
     * 获取列表中指定位置的元素。
     * 该方法直接调用被包装列表的get方法，支持索引访问。
     *
     * @param index 要获取元素的索引位置
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 如果索引超出范围
     */
    @Override
    default E get(int index) throws IndexOutOfBoundsException {
        List<E> list = getSource();
        return list.get(index);
    }

    /**
     * 获取列表中唯一的元素。
     * 如果列表为空或包含多个元素，将抛出相应异常。
     *
     * @return 列表中唯一的元素
     * @throws NoSuchElementException 如果列表为空
     * @throws NoUniqueElementException 如果列表包含多个元素
     */
    @Override
    default E getUnique() throws NoSuchElementException, NoUniqueElementException {
        List<E> list = getSource();
        if (list.isEmpty()) {
            throw new NoSuchElementException();
        }

        if (list.size() != 1) {
            throw new NoUniqueElementException();
        }
        return list.get(0);
    }

    /**
     * 判断列表是否为空。
     * 该方法默认委派给被包装的源列表实现。
     *
     * @return 如果列表为空返回true，否则返回false
     */
    @Override
    default boolean isEmpty() {
        return ListWrapper.super.isEmpty();
    }

    /**
     * 返回列表的迭代器。
     * 该方法默认委派给被包装的源列表实现。
     *
     * @return 列表的迭代器
     */
    @Override
    default Iterator<E> iterator() {
        return ListWrapper.super.iterator();
    }

    /**
     * 返回列表的反向元素集合。
     * 该方法通过CollectionUtils获取反向迭代器，创建新的Elements实例。
     *
     * @return 包含反向元素的Elements集合
     */
    @Override
    default Elements<E> reverse() {
        return Elements.of(() -> CollectionUtils.getIterator(getSource(), true));
    }

    /**
     * 返回列表的顺序流。
     * 该方法默认委派给被包装的源列表实现。
     *
     * @return 列表的顺序流
     */
    @Override
    default Stream<E> stream() {
        return ListWrapper.super.stream();
    }

    /**
     * 返回包含列表所有元素的数组。
     * 该方法默认委派给被包装的源列表实现。
     *
     * @return 包含列表所有元素的数组
     */
    @Override
    default Object[] toArray() {
        return ListWrapper.super.toArray();
    }

    /**
     * 返回包含列表所有元素的指定类型数组。
     * 该方法默认委派给CollectionElementsWrapper的实现。
     *
     * @param array 指定类型的数组
     * @return 包含列表所有元素的指定类型数组
     */
    @Override
    default <T> T[] toArray(T[] array) {
        return CollectionElementsWrapper.super.toArray(array);
    }

    /**
     * 将当前包装器转换为列表元素包装器。
     * 由于当前接口已实现ListElementsWrapper，直接返回自身。
     *
     * @return 当前列表元素包装器实例
     */
    @Override
    default ListElementsWrapper<E, W> toList() {
        return this;
    }
}