package io.basc.framework.util.comparator;

import java.util.Comparator;

/**
 * 颠倒比较规则
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class ReversalComparator<T> implements Comparator<T> {
	private final Comparator<T> comparator;

	public ReversalComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	@Override
	public int compare(T o1, T o2) {
		int v = comparator.compare(o1, o2);
		return v == 0 ? 0 : (-v);
	}

}
