package io.basc.framework.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class RoundRobinSelector<E> implements Selector<E> {
	private final AtomicInteger position = new AtomicInteger();

	@Override
	public E apply(List<E> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		if (list.size() == 1) {
			return list.get(0);
		}

		int pos = Math.abs(position.getAndIncrement());
		pos = pos % list.size();
		return list.get(pos);
	}
}
