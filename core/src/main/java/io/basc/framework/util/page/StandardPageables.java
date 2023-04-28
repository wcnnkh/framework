package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Elements;

public class StandardPageables<K, T> extends PageableWrapper<K, T, Pageable<K, T>> implements Pageables<K, T> {
	private final Function<K, ? extends Pageable<K, T>> processor;

	public StandardPageables(Pageable<K, T> pageable, Function<K, ? extends Pageable<K, T>> processor) {
		super(pageable);
		this.processor = processor;
	}

	public StandardPageables(K cursorId, Function<K, ? extends Pageable<K, T>> processor) {
		this(processor.apply(cursorId), processor);
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		Pageable<K, T> jumpTo = processor.apply(cursorId);
		return new StandardPageables<>(jumpTo, processor);
	}

	@Override
	public <TT> Pageables<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Pageables.super.convert(elementsConverter);
	}

	@Override
	public Pageables<K, T> filter(Predicate<? super T> predicate) {
		return Pageables.super.filter(predicate);
	}

	@Override
	public <TT> Pageables<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return Pageables.super.flatMap(mapper);
	}

	@Override
	public <TK, TT> Pageables<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return Pageables.super.map(cursorIdCodec, elementMapper);
	}

	@Override
	public <TT> Pageables<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return Pageables.super.map(valueMapper);
	}

	@Override
	public <TK, TT> Pageables<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Pageables.super.convert(cursorIdCodec, elementsConverter);
	}
}
