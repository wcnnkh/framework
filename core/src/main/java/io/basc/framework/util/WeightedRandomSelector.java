package io.basc.framework.util;

import java.util.List;

/**
 * 加权随机
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class WeightedRandomSelector<E> implements Selector<E> {
	private static final WeightedRandomSelector<?> SINGLETON = new WeightedRandomSelector<>();

	@Override
	public E apply(List<E> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		if (list.size() == 1) {
			return list.get(0);
		}

		return RandomUtils.random(list, (e) -> (e instanceof Weighted) ? ((Weighted) e).getWeight() : 1, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> WeightedRandomSelector<T> getSingleton() {
		return (WeightedRandomSelector<T>) SINGLETON;
	}
}
