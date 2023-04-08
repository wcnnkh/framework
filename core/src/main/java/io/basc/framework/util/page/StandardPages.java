package io.basc.framework.util.page;

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
	public Pages<K, T> shared() {
		return Pages.super.shared();
	}
}
