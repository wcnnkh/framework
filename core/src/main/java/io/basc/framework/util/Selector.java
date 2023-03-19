package io.basc.framework.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

/**
 * 选择器
 * 
 * @author wcnnkh
 *
 * @param <E> 元素类型
 */
public interface Selector<E> extends Function<Stream<E>, E> {
	@Nullable
	default E apply(Stream<E> elements) {
		if (elements == null) {
			return null;
		}

		return apply(elements.collect(Collectors.toList()));
	}

	@Nullable
	E apply(List<E> elements);

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
	 * 随机选择器
	 * 
	 * @param <T> 任意类型
	 * @return 返回随机选择器
	 */
	public static <T> Selector<T> random() {
		return WeightedRandomSelector.getSingleton();
	}
}
