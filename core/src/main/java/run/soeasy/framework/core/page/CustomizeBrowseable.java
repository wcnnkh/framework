package run.soeasy.framework.core.page;

import java.util.function.Function;
import java.util.function.Predicate;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;

public class CustomizeBrowseable<K, T> implements CursorWrapper<K, T, Cursor<K, T>>, Browseable<K, T> {
	private final Cursor<K, T> source;
	private final Function<K, ? extends Cursor<K, T>> processor;

	public CustomizeBrowseable(Cursor<K, T> source, Function<K, ? extends Cursor<K, T>> processor) {
		this.source = source;
		this.processor = processor;
	}

	@Override
	public Cursor<K, T> getSource() {
		return source;
	}

	public CustomizeBrowseable(K cursorId, Function<K, ? extends Cursor<K, T>> processor) {
		this(processor.apply(cursorId), processor);
	}

	@Override
	public Browseable<K, T> jumpTo(K cursorId) {
		Cursor<K, T> jumpTo = processor.apply(cursorId);
		return new CustomizeBrowseable<>(jumpTo, processor);
	}

	@Override
	public <TT> Browseable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Browseable.super.convert(elementsConverter);
	}

	@Override
	public Browseable<K, T> filter(Predicate<? super T> predicate) {
		return Browseable.super.filter(predicate);
	}

	@Override
	public <TT> Browseable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return Browseable.super.flatMap(mapper);
	}

	@Override
	public <TK, TT> Browseable<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
		return Browseable.super.map(cursorIdCodec, elementMapper);
	}

	@Override
	public <TT> Browseable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return Browseable.super.map(valueMapper);
	}

	@Override
	public <TK, TT> Browseable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Browseable.super.convert(cursorIdCodec, elementsConverter);
	}
}
