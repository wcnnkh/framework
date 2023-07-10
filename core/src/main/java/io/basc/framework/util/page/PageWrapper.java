package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Elements;

public class PageWrapper<K, T, W extends Page<K, T>> extends CursorWrapper<K, T, W> implements Page<K, T> {

	public PageWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public <TT> Page<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(elementsConverter);
	}

	@Override
	public <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(cursorIdConverter, elementsConverter);
	}

	@Override
	public Page<K, T> filter(Predicate<? super T> predicate) {
		return wrappedTarget.filter(predicate);
	}

	@Override
	public <TT> Page<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public long getTotal() {
		return wrappedTarget.getTotal();
	}

	@Override
	public long getPageSize() {
		return wrappedTarget.getPageSize();
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
