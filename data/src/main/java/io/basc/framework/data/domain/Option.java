package io.basc.framework.data.domain;

import java.util.List;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Pair;
import io.basc.framework.util.function.Processor;

public class Option<K, V> extends Pair<K, V> {
	private static final long serialVersionUID = 1L;
	private K parentKey;

	public K getParentKey() {
		return parentKey;
	}

	public void setParentKey(K parentKey) {
		this.parentKey = parentKey;
	}

	public static <K, V, S extends Option<K, V>> List<Tree<S>> parse(List<? extends S> options, @Nullable K parentKey) {
		return parse(options, parentKey, (e) -> e);
	}

	public static <K, V, S extends Option<K, V>, T, E extends Throwable> List<Tree<T>> parse(List<? extends S> options,
			@Nullable K parentKey, Processor<? super S, ? extends T, ? extends E> processor) throws E {
		return Tree.parse(options, Option::getKey, parentKey, Option::getParentKey, processor);
	}
}
