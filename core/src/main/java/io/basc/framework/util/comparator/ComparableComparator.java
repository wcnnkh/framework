package io.basc.framework.util.comparator;

import io.basc.framework.core.OrderComparator;

import java.util.Comparator;

public class ComparableComparator<T> implements Comparator<T> {

	public static final ComparableComparator<Object> INSTANCE = new ComparableComparator<Object>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(T o1, T o2) {
		if (o1 instanceof Comparable) {
			return ((Comparable) o1).compareTo(o2);
		}

		if (o2 instanceof Comparable) {
			return ((Comparable) o2).compareTo(o1);
		}

		return OrderComparator.INSTANCE.compare(o1, o2);
	}
}
