package io.basc.framework.util.page;

import java.util.function.Function;

import io.basc.framework.util.Elements;

public class PagesWrapper<K, T, W extends Pages<K, T>> extends PageablesWrapper<K, T, W> implements Pages<K, T> {

	public PagesWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Pages<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public Elements<? extends Page<K, T>> pages() {
		return wrappedTarget.pages();
	}

	@Override
	public long getTotal() {
		return wrappedTarget.getTotal();
	}

	@Override
	public long getLimit() {
		return wrappedTarget.getLimit();
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId, long count) {
		return wrappedTarget.jumpTo(cursorId, count);
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId) {
		return wrappedTarget.jumpTo(cursorId);
	}

	@Override
	public Pages<K, T> next() {
		return wrappedTarget.next();
	}

	@Override
	public <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(keyMapper, valueMapper);
	}

	@Override
	public <TT> Page<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

	@Override
	public Page<K, T> all() {
		return wrappedTarget.all();
	}
}
