package run.soeasy.framework.core.collection.merge;

import java.util.function.Function;

import run.soeasy.framework.core.collection.Elements;

public class FlatMerger<E> implements Merger<Elements<? extends E>> {
	static final FlatMerger<?> INSTANCE = new FlatMerger<>();

	@Override
	public Elements<E> select(Elements<Elements<? extends E>> elements) {
		return elements.filter((e) -> e != null).flatMap((e) -> e.map(Function.identity()));
	}
}