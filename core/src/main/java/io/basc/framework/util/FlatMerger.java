package io.basc.framework.util;

import java.util.function.Function;

public class FlatMerger<E> implements Merger<Elements<E>> {
	private static final FlatMerger<?> GLOBAL = new FlatMerger<>();

	@SuppressWarnings("unchecked")
	public static <T> FlatMerger<T> global() {
		return (FlatMerger<T>) GLOBAL;
	}

	@Override
	public Elements<E> merge(Elements<? extends Elements<E>> elements) {
		return elements.filter((e) -> e != null).flatMap((e) -> e.map(Function.identity()));
	}
}
