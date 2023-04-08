package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.ConvertibleIterator;

public class ConvertiblePageable<M extends Pageable<SK, ST>, SK, ST, K, T> implements Pageable<K, T> {
	protected final M source;
	private final Function<? super SK, ? extends K> keyMap;
	private final Function<? super ST, ? extends T> valueMap;

	public ConvertiblePageable(M source, Function<? super SK, ? extends K> keyMap,
			Function<? super ST, ? extends T> valueMap) {
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
	public Iterator<T> iterator() {
		return new ConvertibleIterator<>(source.iterator(), valueMap);
	}

	@Override
	public List<T> getList() {
		return source.getList().stream().map(valueMap).collect(Collectors.toList());
	}

	@Override
	public Stream<T> stream() {
		return source.stream().map(valueMap);
	}
}
