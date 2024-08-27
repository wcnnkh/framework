package io.basc.framework.util.select;

import java.util.List;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.RandomUtils;

/**
 * 加权随机
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class WeightedRandomSelector<E> implements Selector<E> {
	public static final WeightedRandomSelector<?> INSTANCE = new WeightedRandomSelector<>();

	@Override
	public E select(Elements<? extends E> elements) {
		if (elements == null) {
			return null;
		}
		List<? extends E> list = elements.toList();
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		if (list.size() == 1) {
			return list.get(0);
		}

		return RandomUtils.random(list, (e) -> (e instanceof Weighted) ? ((Weighted) e).getWeight() : 1, null);
	}
}
