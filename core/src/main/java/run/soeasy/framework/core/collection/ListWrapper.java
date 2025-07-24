package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * 列表包装器接口，用于对列表进行统一封装和操作委托。
 * 该接口继承自List和CollectionWrapper，提供了对底层列表的透明包装，
 * 所有操作默认委托给被包装的源列表对象，支持对列表的所有标准操作。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过包装模式实现对List实例的功能增强和操作代理</li>
 *   <li>所有List接口方法默认委派给被包装的源列表实现</li>
 *   <li>保持与原始列表完全一致的行为和语义</li>
 *   <li>类型安全的泛型设计，确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对现有列表添加额外功能（如日志记录、权限控制）</li>
 *   <li>需要统一处理不同类型的列表实现</li>
 *   <li>需要在不修改原始实现的情况下添加自定义行为</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的列表类型，必须实现List接口
 * @see List
 * @see CollectionWrapper
 */
public interface ListWrapper<E, W extends List<E>> extends List<E>, CollectionWrapper<E, W> {

    // --------------------- List接口方法实现 ---------------------

    @Override
    default boolean add(E e) {
        return getSource().add(e);
    }

    @Override
    default void add(int index, E element) {
        getSource().add(index, element);
    }

    @Override
    default boolean addAll(Collection<? extends E> c) {
        return getSource().addAll(c);
    }

    @Override
    default boolean addAll(int index, Collection<? extends E> c) {
        return getSource().addAll(index, c);
    }

    @Override
    default void clear() {
        getSource().clear();
    }

    @Override
    default boolean contains(Object o) {
        return getSource().contains(o);
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        return getSource().containsAll(c);
    }

    @Override
    default void forEach(Consumer<? super E> action) {
        getSource().forEach(action);
    }

    @Override
    default E get(int index) {
        return getSource().get(index);
    }

    @Override
    default int indexOf(Object o) {
        return getSource().indexOf(o);
    }

    @Override
    default boolean isEmpty() {
        return getSource().isEmpty();
    }

    @Override
    default Iterator<E> iterator() {
        return getSource().iterator();
    }

    @Override
    default int lastIndexOf(Object o) {
        return getSource().lastIndexOf(o);
    }

    @Override
    default ListIterator<E> listIterator() {
        return getSource().listIterator();
    }

    @Override
    default ListIterator<E> listIterator(int index) {
        return getSource().listIterator(index);
    }

    @Override
    default Stream<E> parallelStream() {
        return getSource().parallelStream();
    }

    @Override
    default E remove(int index) {
        return getSource().remove(index);
    }

    @Override
    default boolean remove(Object o) {
        return getSource().remove(o);
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        return getSource().removeAll(c);
    }

    @Override
    default boolean removeIf(Predicate<? super E> filter) {
        return getSource().removeIf(filter);
    }

    @Override
    default void replaceAll(UnaryOperator<E> operator) {
        getSource().replaceAll(operator);
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        return getSource().retainAll(c);
    }

    @Override
    default E set(int index, E element) {
        return getSource().set(index, element);
    }

    @Override
    default int size() {
        return getSource().size();
    }

    @Override
    default void sort(Comparator<? super E> c) {
        getSource().sort(c);
    }

    @Override
    default Spliterator<E> spliterator() {
        return getSource().spliterator();
    }

    @Override
    default Stream<E> stream() {
        return getSource().stream();
    }

    @Override
    default List<E> subList(int fromIndex, int toIndex) {
        return getSource().subList(fromIndex, toIndex);
    }

    @Override
    default Object[] toArray() {
        return getSource().toArray();
    }

    @Override
    default <T> T[] toArray(T[] a) {
        return getSource().toArray(a);
    }
}