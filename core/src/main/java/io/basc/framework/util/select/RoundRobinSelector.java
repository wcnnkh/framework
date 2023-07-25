package io.basc.framework.util.select;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.Elements;

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
	public E apply(Elements<E> elements) {
		if (elements == null) {
			return null;
		}

		List<E> list = elements.toList();
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
