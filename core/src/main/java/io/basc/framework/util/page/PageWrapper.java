package io.basc.framework.util.page;

import java.util.function.Function;

public class PageWrapper<K, T, W extends Page<K, T>> extends PageableWrapper<K, T, W> implements Page<K, T> {

	public PageWrapper(W wrappedTarget) {
		super(wrappedTarget);
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
	public Page<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public <TT> Page<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

	@Override
	public <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(keyMapper, valueMapper);
	}
}
