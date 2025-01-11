package io.basc.framework.util.select;

import java.util.Comparator;

import io.basc.framework.util.Assert;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.comparator.OrderComparator;

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
	public E select(Elements<? extends E> elements) {
		if (elements == null) {
			return null;
		}

		return elements.convert((e) -> e.sorted(comparator)).first();
	}

}
