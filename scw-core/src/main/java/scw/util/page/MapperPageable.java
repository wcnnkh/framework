package scw.util.page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import scw.util.Wrapper;

public class MapperPageable<P extends Pageable<K, S>, K, S, T> extends Wrapper<P> implements Pageable<K, T> {
	private final Function<? super S, ? extends T> mapper;

	public MapperPageable(P pageable, Function<? super S, ? extends T> mapper) {
		super(pageable);
		this.mapper = mapper;
	}

	@Override
	public List<T> rows() {
		return wrappedTarget.rows().stream().map(mapper).collect(Collectors.toList());
	}

	public K getCursorId() {
		return wrappedTarget.getCursorId();
	}

	@Override
	public long getCount() {
		return wrappedTarget.getCount();
	}

	@Override
	public K getNextCursorId() {
		return wrappedTarget.getNextCursorId();
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}
}
