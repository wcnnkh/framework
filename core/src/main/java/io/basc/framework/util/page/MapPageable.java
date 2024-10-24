package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.util.Cursor;

public class MapPageable<M extends Pageable<SK, ST>, SK, ST, K, T> implements Pageable<K, T> {
	protected final M source;
	private final Function<? super SK, ? extends K> keyMap;
	private final Function<? super ST, ? extends T> valueMap;

	public MapPageable(M source, Function<? super SK, ? extends K> keyMap, Function<? super ST, ? extends T> valueMap) {
		this.source = source;
		this.keyMap = keyMap;
		this.valueMap = valueMap;
	}

	@Override
	public K getCursorId() {
		SK value = source.getCursorId();
		return value == null ? null : keyMap.apply(value);
	}

	@Override
	public K getNextCursorId() {
		SK value = source.getNextCursorId();
		return value == null ? null : keyMap.apply(value);
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public Cursor<T> iterator() {
		return source.iterator().map(valueMap);
	}
}
