package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.element.Elements;

public class PageableWrapper<K, T, W extends Pageable<K, T>> extends BrowsableWrapper<K, T, W> implements Pageable<K, T> {

	public PageableWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Page<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(cursorIdConverter, elementsConverter);
	}

	@Override
	public <TT> Pageable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(elementsConverter);
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
	public long getPageSize() {
		return wrappedTarget.getPageSize();
	}

	@Override
	public Pageable<K, T> jumpTo(K cursorId, long count) {
		return wrappedTarget.jumpTo(cursorId, count);
	}

	@Override
	public Pageable<K, T> jumpTo(K cursorId) {
		return wrappedTarget.jumpTo(cursorId);
	}

	@Override
	public Pageable<K, T> next() {
		return wrappedTarget.next();
	}

	@Override
	public <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
			Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(keyMapper, valueMapper);
	}

	@Override
	public <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

	@Override
	public Page<K, T> all() {
		return wrappedTarget.all();
	}

	@Override
	public <TK, TT> Pageable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(cursorIdCodec, elementsConverter);
	}

	@Override
	public Pageable<K, T> filter(Predicate<? super T> predicate) {
		return wrappedTarget.filter(predicate);
	}

	@Override
	public <TT> Pageable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public <TK, TT> Pageable<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return wrappedTarget.map(cursorIdCodec, elementMapper);
	}

	@Override
	public Paginations<T> toPaginations(long start, long limit) {
		return wrappedTarget.toPaginations(start, limit);
	}
}
