package io.basc.framework.util.page;

import java.util.function.Function;

public class MapPage<M extends Page<SK, ST>, SK, ST, K, T> extends MapPageable<M, SK, ST, K, T> implements Page<K, T> {

	public MapPage(M source, Function<? super SK, K> keyMap, Function<? super ST, T> valueMap) {
		super(source, keyMap, valueMap);
	}

	@Override
	public long getTotal() {
		return source.getTotal();
	}

	@Override
	public long getCount() {
		return source.getCount();
	}
}
