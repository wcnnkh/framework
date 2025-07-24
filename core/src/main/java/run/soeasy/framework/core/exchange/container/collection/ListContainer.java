package run.soeasy.framework.core.exchange.container.collection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 列表容器实现，基于列表数据结构提供元素注册与管理功能。
 * <p>
 * 该容器继承自{@link CollectionContainer}并实现{@link List}接口，
 * 支持元素的有序存储和随机访问，同时具备注册生命周期管理和事件发布能力。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>列表操作支持：完全实现{@link List}接口的所有方法</li>
 *   <li>索引访问：通过索引快速访问和操作元素</li>
 *   <li>注册生命周期管理：自动处理元素注册的启动、停止和取消</li>
 *   <li>事件驱动：元素变更时自动触发相应事件</li>
 * </ul>
 *
 * @param <E> 注册元素的类型
 * @param <C> 存储注册元素的列表类型，需继承{@link List}<{@link ElementRegistration}<{@link E}>>
 * 
 * @author soeasy.run
 * @see CollectionContainer
 * @see List
 */
public class ListContainer<E, C extends List<ElementRegistration<E>>> extends CollectionContainer<E, C>
        implements List<E> {

    /**
     * 构造函数，初始化列表容器
     * <p>
     * 通过供给函数获取列表实例，确保容器的存储结构。
     * 
     * @param containerSource 列表实例的供给函数，不可为null
     * @throws NullPointerException 若containerSource为null
     */
    public ListContainer(@NonNull ThrowingSupplier<? extends C, ? extends RuntimeException> containerSource) {
        super(containerSource);
    }

    /**
     * 在指定位置插入所有元素并注册
     * <p>
     * 若注册失败，元素不会被添加到列表中。
     * 成功注册的元素会触发创建事件。
     * 
     * @param index 插入位置的索引
     * @param c 要插入的元素集合
     * @return true表示列表被修改，false表示未修改
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    @Override
    public final boolean addAll(int index, Collection<? extends E> c) {
        return !registers(c, (list, elements) -> {
            list.addAll(index, elements.toList());
        }, getPublisher()).getElements().isEmpty();
    }

    /**
     * 获取指定位置的元素
     * <p>
     * 通过索引访问列表中的元素，返回注册的载荷数据。
     * 
     * @param index 元素的索引
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    @Override
    public final E get(int index) {
        return read((list) -> {
            ElementRegistration<E> registration = list.get(index);
            return registration == null ? null : registration.getPayload();
        });
    }

    /**
     * 获取指定位置的注册对象
     * <p>
     * 通过索引访问列表中的注册对象，可用于进一步操作注册状态。
     * 
     * @param index 注册对象的索引
     * @return 指定位置的注册对象
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    public final ElementRegistration<E> getRegistration(int index) {
        return getRegistration((list) -> {
            if (list == null) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + 0);
            }
            return list.get(index);
        });
    }

    /**
     * 替换指定位置的元素
     * <p>
     * 更新指定索引处的元素，并触发更新事件。
     * 
     * @param index 要替换元素的索引
     * @param element 新元素
     * @return 被替换的旧元素
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    @Override
    public final E set(int index, E element) {
        ElementRegistration<E> elementRegistration = getRegistration(index);
        return elementRegistration.setPayload(element);
    }

    /**
     * 在指定位置插入元素
     * <p>
     * 在指定索引处插入新元素并注册，触发创建事件。
     * 
     * @param index 插入位置的索引
     * @param element 要插入的元素
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    @Override
    public final void add(int index, E element) {
        registers(Elements.singleton(element), (list, elements) -> {
            elements.forEach((e) -> list.add(index, e));
        }, getPublisher());
    }

    /**
     * 移除指定位置的元素
     * <p>
     * 取消指定索引处的元素注册并触发删除事件。
     * 
     * @param index 要移除元素的索引
     * @return 被移除的元素
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    @Override
    public final E remove(int index) {
        ElementRegistration<E> elementRegistration = getRegistration(index);
        elementRegistration.cancel();
        return elementRegistration.getPayload();
    }

    /**
     * 返回元素在列表中首次出现的索引
     * <p>
     * 搜索元素在列表中的位置，比较基于元素的equals方法。
     * 
     * @param o 要查找的元素
     * @return 元素首次出现的索引，若不存在则返回-1
     */
    @Override
    public int indexOf(Object o) {
        return readAsInt((list) -> list == null ? -1 : list.indexOf(o));
    }

    /**
     * 返回元素在列表中最后一次出现的索引
     * <p>
     * 从后向前搜索元素在列表中的位置，比较基于元素的equals方法。
     * 
     * @param o 要查找的元素
     * @return 元素最后一次出现的索引，若不存在则返回-1
     */
    @Override
    public final int lastIndexOf(Object o) {
        return readAsInt((list) -> list == null ? -1 : list.lastIndexOf(o));
    }

    /**
     * 返回列表的列表迭代器
     * <p>
     * 迭代器遍历列表中的元素载荷，支持双向遍历。
     * 
     * @return 列表迭代器
     */
    @Override
    public final ListIterator<E> listIterator() {
        return toList().listIterator();
    }

    /**
     * 返回从指定位置开始的列表迭代器
     * <p>
     * 迭代器从指定索引开始遍历列表中的元素载荷，支持双向遍历。
     * 
     * @param index 起始索引
     * @return 列表迭代器
     * @throws IndexOutOfBoundsException 若索引超出范围
     */
    @Override
    public final ListIterator<E> listIterator(int index) {
        return toList().listIterator(index);
    }

    /**
     * 返回列表的子列表
     * <p>
     * 返回从fromIndex（包含）到toIndex（不包含）的子列表视图。
     * 
     * @param fromIndex 起始索引（包含）
     * @param toIndex 结束索引（不包含）
     * @return 子列表
     * @throws IndexOutOfBoundsException 若索引超出范围
     * @throws IllegalArgumentException 若fromIndex大于toIndex
     */
    @Override
    public final List<E> subList(int fromIndex, int toIndex) {
        return toList().subList(fromIndex, toIndex);
    }
}