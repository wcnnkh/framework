package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 提供对Collection对象的包装功能，允许对集合进行封装并操作原始集合对象。
 * 实现此接口的类可以将Collection对象包装起来，提供额外的功能或修改默认行为。
 *
 * @param <E> 集合元素的类型
 * @param <W> 被包装的Collection类型
 * 
 * @author soeasy.run
 */
@FunctionalInterface
public interface CollectionWrapper<E, W extends Collection<E>> extends Collection<E>, IterableWrapper<E, W> {

	/**
	 * 返回被包装集合中的元素数量。
	 *
	 * @return 集合中的元素数量
	 */
	@Override
	default int size() {
		return getSource().size();
	}

	/**
	 * 判断被包装的集合是否为空。
	 *
	 * @return 如果集合不包含元素，则返回true
	 */
	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}

	/**
	 * 判断被包装的集合是否包含指定的元素。
	 *
	 * @param o 要测试其是否存在于集合中的元素
	 * @return 如果集合包含指定的元素，则返回true
	 */
	@Override
	default boolean contains(Object o) {
		return getSource().contains(o);
	}

	/**
	 * 返回一个迭代器，用于遍历被包装集合中的元素。
	 *
	 * @return 一个迭代器实例
	 */
	@Override
	default Iterator<E> iterator() {
		return getSource().iterator();
	}

	/**
	 * 返回包含被包装集合中所有元素的数组。
	 *
	 * @return 包含集合中所有元素的数组
	 */
	@Override
	default Object[] toArray() {
		return getSource().toArray();
	}

	/**
	 * 返回包含被包装集合中所有元素的数组； 返回数组的运行时类型是指定数组的类型。
	 *
	 * @param <T> 数组元素的类型
	 * @param a   如果指定数组足够大，则将集合元素存储在此数组中； 否则，将为指定数组的运行时类型和此集合的大小分配一个新数组。
	 * @return 包含集合元素的数组
	 */
	@Override
	default <T> T[] toArray(T[] a) {
		return getSource().toArray(a);
	}

	/**
	 * 向被包装的集合中添加指定的元素（可选操作）。
	 *
	 * @param e 要添加到集合中的元素
	 * @return 如果集合因调用而发生更改，则返回true
	 */
	@Override
	default boolean add(E e) {
		return getSource().add(e);
	}

	/**
	 * 从被包装的集合中移除指定元素的单个实例（如果存在）（可选操作）。
	 *
	 * @param o 要从集合中移除的元素（如果存在）
	 * @return 如果集合因调用而发生更改，则返回true
	 */
	@Override
	default boolean remove(Object o) {
		return getSource().remove(o);
	}

	/**
	 * 判断被包装的集合是否包含指定集合中的所有元素。
	 *
	 * @param c 要测试其是否存在于集合中的集合
	 * @return 如果集合包含指定集合中的所有元素，则返回true
	 */
	@Override
	default boolean containsAll(Collection<?> c) {
		return getSource().containsAll(c);
	}

	/**
	 * 将指定集合中的所有元素添加到被包装的集合中（可选操作）。
	 *
	 * @param c 包含要添加到此集合的元素的集合
	 * @return 如果集合因调用而发生更改，则返回true
	 */
	@Override
	default boolean addAll(Collection<? extends E> c) {
		return getSource().addAll(c);
	}

	/**
	 * 仅保留被包装的集合中包含在指定集合中的元素（可选操作）。
	 *
	 * @param c 包含要保留在此集合中的元素的集合
	 * @return 如果集合因调用而发生更改，则返回true
	 */
	@Override
	default boolean retainAll(Collection<?> c) {
		return getSource().retainAll(c);
	}

	/**
	 * 移除被包装集合中的所有元素（可选操作）。
	 *
	 */
	@Override
	default void clear() {
		getSource().clear();
	}

	/**
	 * 返回一个可能并行的Stream，用于遍历被包装的集合。
	 *
	 * @return 一个可能并行的Stream
	 */
	@Override
	default Stream<E> parallelStream() {
		return getSource().parallelStream();
	}

	/**
	 * 移除被包装集合中所有满足给定谓词的元素。
	 *
	 * @param filter 一个谓词，用于测试元素是否应被移除
	 * @return 如果任何元素被移除，则返回true
	 */
	@Override
	default boolean removeIf(Predicate<? super E> filter) {
		return getSource().removeIf(filter);
	}

	/**
	 * 为被包装的集合创建一个Spliterator。
	 *
	 * @return 一个Spliterator实例
	 */
	@Override
	default Spliterator<E> spliterator() {
		return getSource().spliterator();
	}

	/**
	 * 返回一个顺序Stream，用于遍历被包装的集合。
	 *
	 * @return 一个顺序Stream
	 */
	@Override
	default Stream<E> stream() {
		return getSource().stream();
	}

	/**
	 * 从被包装的集合中移除指定集合中包含的所有元素（可选操作）。
	 *
	 * @param c 包含要从此集合中移除的元素的集合
	 * @return 如果集合因调用而发生更改，则返回true
	 */
	@Override
	default boolean removeAll(Collection<?> c) {
		return getSource().removeAll(c);
	}
}