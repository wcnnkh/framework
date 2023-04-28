package io.basc.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Elements;

public class StandardPages<K, T> extends PageWrapper<K, T, Page<K, T>> implements Pages<K, T> {
	private final PageableProcessor<K, T> processor;

	public StandardPages(Page<K, T> page, PageableProcessor<K, T> processor) {
		super(page);
		this.processor = processor;
	}

	public StandardPages(long total, K cursorId, long count, PageableProcessor<K, T> processor) {
		this(new StandardPage<K, T>(total, count, processor.process(cursorId, count)), processor);
	}

	public PageableProcessor<K, T> getProcessor() {
		return processor;
	}

	@Override
	public Pages<K, T> jumpTo(K cursorId, long count) {
		return new StandardPages<>(getTotal(), cursorId, count, processor);
	}

	@Override
	public <TK, TT> Pages<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Pages.super.convert(cursorIdCodec, elementsConverter);
	}

	@Override
	public <TT> Pages<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return Pages.super.convert(elementsConverter);
	}

	@Override
	public Pages<K, T> filter(Predicate<? super T> predicate) {
		return Pages.super.filter(predicate);
	}

	@Override
	public <TT> Pages<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		return Pages.super.flatMap(mapper);
	}

	@Override
	public <TT> Pages<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
		return Pages.super.map(valueMapper);
	}
}
