package run.soeasy.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.collection.Elements;

public class StandardPageable<K, T> implements PageWrapper<K, T, Page<K, T>>, Pageable<K, T> {
	private final Page<K, T> source;
	private final CursorProcessor<K, T> processor;

	public StandardPageable(Page<K, T> source, CursorProcessor<K, T> processor) {
		this.source = source;
		this.processor = processor;
	}

	public Page<K, T> getSource() {
		return source;
	}

	public StandardPageable(long total, K cursorId, long count, CursorProcessor<K, T> processor) {
		this(new StandardPage<K, T>(total, count, processor.process(cursorId, count)), processor);
	}

	public CursorProcessor<K, T> getProcessor() {
		return processor;
	}

	@Override
	public Pageable<K, T> jumpTo(K cursorId, long count) {
		return new StandardPageable<>(getTotal(), cursorId, count, processor);
	}

	@Override
	public <TK, TT> Pageable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Pageable.super.convert(cursorIdCodec, elementsConverter);
	}

	@Override
	public <TT> Pageable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Pageable.super.convert(elementsConverter);
	}

	@Override
	public Pageable<K, T> filter(Predicate<? super T> predicate) {
		return Pageable.super.filter(predicate);
	}

	@Override
	public <TT> Pageable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return Pageable.super.flatMap(mapper);
	}

	@Override
	public <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return Pageable.super.map(valueMapper);
	}
}
