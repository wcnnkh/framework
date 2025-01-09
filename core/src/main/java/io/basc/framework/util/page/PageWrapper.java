package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.collection.Elements;

public interface PageWrapper<K, T, W extends Page<K, T>> extends CursorWrapper<K, T, W>, Page<K, T> {

	@Override
	default <TT> Page<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(elementsConverter);
	}

	@Override
	default <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(cursorIdConverter, elementsConverter);
	}

	@Override
	default Page<K, T> filter(Predicate<? super T> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <TT> Page<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return getSource().flatMap(mapper);
	}

	@Override
	default boolean hasNext() {
		return getSource().hasNext();
	}

	@Override
	default long getTotal() {
		return getSource().getTotal();
	}

	@Override
	default long getPageSize() {
		return getSource().getPageSize();
	}

	@Override
	default Page<K, T> shared() {
		return getSource().shared();
	}

	@Override
	default <TT> Page<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(valueMapper);
	}

	@Override
	default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(keyMapper, valueMapper);
	}
}
