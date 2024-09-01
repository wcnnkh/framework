package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;

public interface CursorWrapper<K, T, W extends Cursor<K, T>> extends Cursor<K, T>, Wrapper<W> {

	@Override
	default K getCursorId() {
		return getSource().getCursorId();
	}

	@Override
	default Elements<T> getElements() {
		return getSource().getElements();
	}

	@Override
	default boolean hasNext() {
		return getSource().hasNext();
	}

	@Override
	default <TK, TT> Cursor<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(keyMapper, valueMapper);
	}

	@Override
	default <TT> Cursor<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(valueMapper);
	}

	@Override
	default Cursor<K, T> shared() {
		return getSource().shared();
	}

	@Override
	default K getNextCursorId() {
		return getSource().getNextCursorId();
	}

	@Override
	default <TT> Cursor<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(elementsConverter);
	}

	@Override
	default <TK, TT> Cursor<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(cursorIdConverter, elementsConverter);
	}

	@Override
	default Cursor<K, T> filter(Predicate<? super T> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <TT> Cursor<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return getSource().flatMap(mapper);
	}
}
