package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;

public class CursorWrapper<K, T, W extends Cursor<K, T>> extends Wrapper<W> implements Cursor<K, T> {

	public CursorWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public K getCursorId() {
		return wrappedTarget.getCursorId();
	}

	@Override
	public Elements<T> getElements() {
		return wrappedTarget.getElements();
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public <TK, TT> Cursor<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(keyMapper, valueMapper);
	}

	@Override
	public <TT> Cursor<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

	@Override
	public Cursor<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public K getNextCursorId() {
		return wrappedTarget.getNextCursorId();
	}

	@Override
	public <TT> Cursor<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(elementsConverter);
	}

	@Override
	public <TK, TT> Cursor<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(cursorIdConverter, elementsConverter);
	}

	@Override
	public Cursor<K, T> filter(Predicate<? super T> predicate) {
		return wrappedTarget.filter(predicate);
	}

	@Override
	public <TT> Cursor<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}
}
