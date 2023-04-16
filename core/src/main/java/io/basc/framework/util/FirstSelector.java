package io.basc.framework.util;

import java.util.Comparator;

import io.basc.framework.core.OrderComparator;

public class FirstSelector<E> implements Selector<E> {
	public static FirstSelector<?> INSTANCE = new FirstSelector<>(OrderComparator.INSTANCE);

	private final Comparator<? super E> comparator;

	public FirstSelector(Comparator<? super E> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		this.comparator = comparator;
	}

	public Comparator<? super E> getComparator() {
		return comparator;
	}

	@Override
	public E apply(Elements<E> elements) {
		if (elements == null) {
			return null;
		}

		return elements.convert((e) -> e.sorted(comparator)).first();
	}

}
