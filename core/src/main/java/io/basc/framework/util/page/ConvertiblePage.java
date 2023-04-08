package io.basc.framework.util.page;

import java.util.function.Function;

public class ConvertiblePage<M extends Page<SK, ST>, SK, ST, K, T> extends ConvertiblePageable<M, SK, ST, K, T>
		implements Page<K, T> {

	public ConvertiblePage(M source, Function<? super SK, ? extends K> keyMap,
			Function<? super ST, ? extends T> valueMap) {
		super(source, keyMap, valueMap);
	}

	@Override
	public long getTotal() {
		return source.getTotal();
	}

	@Override
	public long getLimit() {
		return source.getLimit();
	}
}
