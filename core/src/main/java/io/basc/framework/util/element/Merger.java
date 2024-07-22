package io.basc.framework.util.element;

import java.util.function.Function;

import io.basc.framework.util.select.Selector;

public class Merger<T> implements Selector<Elements<T>> {
	private static final Merger<?> GLOBAL = new Merger<>();

	@Override
	public Elements<T> apply(Elements<? extends Elements<T>> elements) {
		return elements.filter((e) -> e != null).flatMap((e) -> e.map(Function.identity()));
	}

	@SuppressWarnings("unchecked")
	public static <T> Merger<T> global() {
		return (Merger<T>) GLOBAL;
	}
}
