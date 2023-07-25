package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.element.Elements;

public class StandardBrowsable<K, T> extends CursorWrapper<K, T, Cursor<K, T>> implements Browsable<K, T> {
	private final Function<K, ? extends Cursor<K, T>> processor;

	public StandardBrowsable(Cursor<K, T> pageable, Function<K, ? extends Cursor<K, T>> processor) {
		super(pageable);
		this.processor = processor;
	}

	public StandardBrowsable(K cursorId, Function<K, ? extends Cursor<K, T>> processor) {
		this(processor.apply(cursorId), processor);
	}

	@Override
	public Browsable<K, T> jumpTo(K cursorId) {
		Cursor<K, T> jumpTo = processor.apply(cursorId);
		return new StandardBrowsable<>(jumpTo, processor);
	}

	@Override
	public <TT> Browsable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Browsable.super.convert(elementsConverter);
	}

	@Override
	public Browsable<K, T> filter(Predicate<? super T> predicate) {
		return Browsable.super.filter(predicate);
	}

	@Override
	public <TT> Browsable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return Browsable.super.flatMap(mapper);
	}

	@Override
	public <TK, TT> Browsable<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return Browsable.super.map(cursorIdCodec, elementMapper);
	}

	@Override
	public <TT> Browsable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return Browsable.super.map(valueMapper);
	}

	@Override
	public <TK, TT> Browsable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Browsable.super.convert(cursorIdCodec, elementsConverter);
	}
}
