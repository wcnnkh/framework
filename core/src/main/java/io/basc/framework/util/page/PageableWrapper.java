package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Elements;

public interface PageableWrapper<K, T, W extends Pageable<K, T>>
		extends BrowseableWrapper<K, T, W>, PageWrapper<K, T, W>, Pageable<K, T> {

	@Override
	default Page<K, T> shared() {
		return getSource().shared();
	}

	@Override
	default <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(cursorIdConverter, elementsConverter);
	}

	@Override
	default <TT> Pageable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(elementsConverter);
	}

	@Override
	default Elements<? extends Page<K, T>> pages() {
		return getSource().pages();
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
	default Pageable<K, T> jumpTo(K cursorId, long count) {
		return getSource().jumpTo(cursorId, count);
	}

	@Override
	default Pageable<K, T> jumpTo(K cursorId) {
		return getSource().jumpTo(cursorId);
	}

	@Override
	default Pageable<K, T> next() {
		return getSource().next();
	}

	@Override
	default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(keyMapper, valueMapper);
	}

	@Override
	default <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return getSource().map(valueMapper);
	}

	@Override
	default Page<K, T> all() {
		return getSource().all();
	}

	@Override
	default <TK, TT> Pageable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return getSource().convert(cursorIdCodec, elementsConverter);
	}

	@Override
	default Pageable<K, T> filter(Predicate<? super T> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <TT> Pageable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return getSource().flatMap(mapper);
	}

	@Override
	default boolean hasNext() {
		return getSource().hasNext();
	}

	@Override
	default <TK, TT> Pageable<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return getSource().map(cursorIdCodec, elementMapper);
	}

	@Override
	default Paginations<T> toPaginations(long start, long limit) {
		return getSource().toPaginations(start, limit);
	}
}
