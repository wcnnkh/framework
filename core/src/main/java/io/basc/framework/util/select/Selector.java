package io.basc.framework.util.select;

import io.basc.framework.util.element.Elements;

/**
 * 选择器
 * 
 * @author wcnnkh
 *
 * @param <E> 元素类型
 */
@FunctionalInterface
public interface Selector<E> extends Dispatcher<E>, Merger<E> {
	/**
	 * 选择第一个
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Selector<T> first() {
		return (Selector<T>) FirstSelector.INSTANCE;
	}

	/**
	 * 随机选择器
	 * 
	 * @param <T> 任意类型
	 * @return 返回随机选择器
	 */
	@SuppressWarnings("unchecked")
	public static <T> Selector<T> random() {
		return (Selector<T>) WeightedRandomSelector.INSTANCE;
	}

	/**
	 * 轮询选择器
	 * 
	 * @param <T> 任意类型
	 * @return 返回轮询选择器
	 */
	public static <T> Selector<T> roundRobin() {
		return new RoundRobinSelector<>();
	}

	/**
	 * 选择器也是一种特殊的分发器
	 */
	@Override
	default Elements<E> dispatch(Elements<? extends E> elements) {
		E element = select(elements);
		return Elements.singleton(element);
	}

	/**
	 * 选择器也是一种特殊的合并器
	 */
	@Override
	default E merge(Elements<? extends E> elements) {
		return select(elements);
	}

	/**
	 * 从多个中选择一个
	 * 
	 * @param elements
	 * @return
	 */
	E select(Elements<? extends E> elements);
}
