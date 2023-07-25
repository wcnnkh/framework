package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.element.Elements;

public class BrowsableWrapper<K, T, W extends Browsable<K, T>> extends CursorWrapper<K, T, W>
		implements Browsable<K, T> {

	public BrowsableWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	public Cursor<K, T> shared() {
		return wrappedTarget.shared();
	}

	@Override
	public Browsable<K, T> jumpTo(K cursorId) {
		return wrappedTarget.jumpTo(cursorId);
	}

	@Override
	public Browsable<K, T> next() {
		return wrappedTarget.next();
	}

	@Override
	public Cursor<K, T> all() {
		return wrappedTarget.all();
	}

	@Override
	public Elements<? extends Cursor<K, T>> pages() {
		return wrappedTarget.pages();
	}

	@Override
	public <TK, TT> Browsable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(cursorIdCodec, elementsConverter);
	}

	@Override
	public <TT> Browsable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return wrappedTarget.convert(elementsConverter);
	}

	@Override
	public Browsable<K, T> filter(Predicate<? super T> predicate) {
		return wrappedTarget.filter(predicate);
	}

	@Override
	public <TT> Browsable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return wrappedTarget.flatMap(mapper);
	}

	@Override
	public boolean hasNext() {
		return wrappedTarget.hasNext();
	}

	@Override
	public <TK, TT> Browsable<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return wrappedTarget.map(cursorIdCodec, elementMapper);
	}

	@Override
	public <TT> Browsable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return wrappedTarget.map(valueMapper);
	}

}
