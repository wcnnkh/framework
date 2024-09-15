package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Elements;

public interface BrowseableWrapper<K, T, W extends Browseable<K, T>> extends CursorWrapper<K, T, W>, Browseable<K, T> {

	default Cursor<K, T> shared() {
		return getSource().shared();
	}

	@Override
	default Browseable<K, T> jumpTo(K cursorId) {
		return getSource().jumpTo(cursorId);
	}

	@Override
	default Browseable<K, T> next() {
		return getSource().next();
	}

	@Override
	default Cursor<K, T> all() {
		return getSource().all();
	}

	@Override
	default Elements<? extends Cursor<K, T>> pages() {
		return getSource().pages();
	}

	@Override
	default <TK, TT> Browseable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(cursorIdCodec, elementsConverter);
	}

	@Override
	default <TT> Browseable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(elementsConverter);
	}

	@Override
	default Browseable<K, T> filter(Predicate<? super T> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <TT> Browseable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return getSource().flatMap(mapper);
	}

	@Override
	default boolean hasNext() {
		return getSource().hasNext();
	}

	@Override
	default <TK, TT> Browseable<TK, TT> map(Codec<K, TK> cursorIdCodec,
			Function<? super T, ? extends TT> elementMapper) {
		return getSource().map(cursorIdCodec, elementMapper);
	}

	@Override
	default <TT> Browseable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(valueMapper);
	}

}
