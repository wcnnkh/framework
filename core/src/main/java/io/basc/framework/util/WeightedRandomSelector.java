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

	@Override
	public E select(List<E> list) {
		return RandomUtils.random(list, (e) -> (e instanceof Weighted) ? ((Weighted) e).getWeight() : 1, null);
	}

}
