package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Elements;

public class PageablesWrapper<K, T, W extends Pageables<K, T>> extends PageableWrapper<K, T, W>
		implements Pageables<K, T> {

	public PageablesWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	public Pageable<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		return wrappedTarget.jumpTo(cursorId);
	}

	@Override
	public Pageables<K, T> next() {
		return wrappedTarget.next();
	}

	@Override
	public Pageable<K, T> all() {
		return wrappedTarget.all();
	}

	@Override
	public Elements<? extends Pageable<K, T>> pages() {
		return wrappedTarget.pages();
	}

	@Override
	public <TK, TT> Pageables<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(cursorIdCodec, elementsConverter);
	}

	@Override
	public <TT> Pageables<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(elementsConverter);
	}

	@Override
	public Pageables<K, T> filter(Predicate<? super T> predicate) {
		return wrappedTarget.filter(predicate);
	}

	@Override
	public <TT> Pageables<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public <TK, TT> Pageables<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return wrappedTarget.map(cursorIdCodec, elementMapper);
	}

	@Override
	public <TT> Pageables<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

}
