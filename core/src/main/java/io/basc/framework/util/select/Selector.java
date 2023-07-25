package io.basc.framework.util.select;

import java.util.function.Function;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;

/**
 * 选择器
 * 
 * @author wcnnkh
 *
 * @param <E> 元素类型
 */
@FunctionalInterface
public interface Selector<E> extends Function<Elements<E>, E> {
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

	@Nullable
	E apply(Elements<E> elements);
}
