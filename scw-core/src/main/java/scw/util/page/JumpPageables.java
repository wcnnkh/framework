package scw.util.page;

import java.util.Iterator;
import java.util.function.Function;

public class JumpPageables<K, T> extends PageableWrapper<Pageable<K, T>, K, T>
		implements Pageables<K, T>, Iterator<Pageable<K, T>> {
	private final PageableProcessor<K, T> processor;

	public JumpPageables(PageableProcessor<K, T> processor, Pageable<K, T> pageable) {
		super(pageable);
		this.processor = processor;
	}

	@Override
	public PageableProcessor<K, T> getProcessor() {
		return processor;
	}

	@Override
	public Pageables<K, T> next(PageableProcessor<K, T> processor) {
		return Pageables.super.next(processor);
	}
}
