package io.basc.framework.util.element;

import io.basc.framework.util.select.Selector;

public class Merger<T> implements Selector<Elements<T>> {
	private static final Merger<?> GLOBAL = new Merger<>();

	@Override
	public Elements<T> apply(Elements<? extends Elements<T>> elements) {
		return elements.flatMap((e) -> e);
	}

	@SuppressWarnings("unchecked")
	public static <T> Merger<T> global() {
		return (Merger<T>) GLOBAL;
	}
}
