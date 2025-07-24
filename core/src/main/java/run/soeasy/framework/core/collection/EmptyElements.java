package run.soeasy.framework.core.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 空元素集合的具体实现，继承自EmptyStreamable并实现Elements和Listable接口。
 * 该类采用单例模式，提供表示空元素集合的统一实例，用于避免空指针检查和简化代码逻辑。
 * 
 * @author soeasy.run
 * @param <E> 元素类型（实际无元素）
 * @see Elements
 * @see Listable
 * @see EmptyStreamable
 */
class EmptyElements<E> extends EmptyStreamable<E> implements Elements<E>, Listable<E> {
	private static final long serialVersionUID = 1L;

	/**
	 * 空元素集合的单例实例，用于表示不包含任何元素的集合。 该实例为类型安全的泛型对象，推荐直接使用此实例以避免重复创建空集合对象。
	 */
	static final EmptyElements<Object> EMPTY_ELEMENTS = new EmptyElements<>();

	/**
	 * 返回一个空的Provider实例，该实例不包含任何元素。
	 * 
	 * @return 空的Provider实例
	 */
	@Override
	public Provider<E> cacheable() {
		return Provider.empty();
	}

	/**
	 * 返回当前空元素集合的克隆实例。 由于空集合是不可变的，因此直接返回自身实例。
	 * 
	 * @return 当前空元素集合实例
	 */
	@Override
	public Elements<E> clone() {
		return this;
	}

	/**
	 * 对空元素集合执行过滤操作，由于集合中无元素，过滤结果仍为空集合。
	 * 
	 * @param predicate 过滤条件（该参数实际不会被使用）
	 * @return 当前空元素集合实例
	 */
	@Override
	public Elements<E> filter(Predicate<? super E> predicate) {
		return this;
	}

	/**
	 * 判断元素集合是否为空。
	 * 
	 * @return 始终返回true，因为是空集合
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

	/**
	 * 返回一个空的迭代器，不包含任何元素。
	 * 
	 * @return 空的Iterator实例
	 */
	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	/**
	 * 对空元素集合执行映射操作，由于集合中无元素，映射结果仍为空集合。
	 * 
	 * @param mapper 元素映射函数（该参数实际不会被使用）
	 * @param <U>    映射后的元素类型
	 * @return 当前空元素集合实例（类型安全转换）
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return (Elements<U>) this;
	}

	/**
	 * 对空元素集合执行反转操作，由于集合中无元素，反转结果仍为空集合。
	 * 
	 * @return 当前空元素集合实例
	 */
	@Override
	public Elements<E> reverse() {
		return this;
	}

	/**
	 * 返回一个空的Stream实例，不包含任何元素。
	 * 
	 * @return 空的Stream实例
	 */
	@Override
	public Stream<E> stream() {
		return Stream.empty();
	}

	/**
	 * 将空元素集合转换为List包装器，内部包含一个空List。
	 * 
	 * @return 包含空List的ListElementsWrapper实例
	 */
	@Override
	public ListElementsWrapper<E, ?> toList() {
		return new StandardListElements<>(Collections.emptyList());
	}

	/**
	 * 将空元素集合转换为Set包装器，内部包含一个空Set。
	 * 
	 * @return 包含空Set的SetElementsWrapper实例
	 */
	@Override
	public SetElementsWrapper<E, ?> toSet() {
		return new StandardSetElements<>(Collections.emptySet());
	}

	/**
	 * 判断元素集合是否包含元素。
	 * 
	 * @return 始终返回false，因为是空集合
	 */
	@Override
	public final boolean hasElements() {
		return false;
	}

	/**
	 * 返回当前元素集合实例（空集合）。
	 * 
	 * @return 当前空元素集合实例
	 */
	@Override
	public final Elements<E> getElements() {
		return this;
	}
}