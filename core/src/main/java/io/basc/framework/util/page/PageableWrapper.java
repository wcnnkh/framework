package io.basc.framework.util.page;

import java.util.List;
import java.util.function.Function;

import io.basc.framework.util.ElementsWrapper;

public class PageableWrapper<K, T, W extends Pageable<K, T>> extends ElementsWrapper<T, W> implements Pageable<K, T> {

	public PageableWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public K getCursorId() {
		return wrappedTarget.getCursorId();
	}

	@Override
	public K getNextCursorId() {
		return wrappedTarget.getNextCursorId();
	}

	@Override
	public List<T> getList() {
		return wrappedTarget.getList();
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public <TK, TT> Pageable<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(keyMapper, valueMapper);
	}

	@Override
	public <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

	@Override
	public Pageable<K, T> shared() {
		return wrappedTarget.shared();
	}
}
