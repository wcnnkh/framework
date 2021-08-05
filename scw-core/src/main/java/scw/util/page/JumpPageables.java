package scw.util.page;

import java.util.Iterator;

public class JumpPageables<K, T> extends PageableWrapper<Pageable<K, T>, K, T>
		implements Pageables<K, T>, Iterator<Pageable<K, T>> {
	private final PageableProcessor<K, T> processor;
	
	public JumpPageables(Pageable<K, T> pageable, PageableProcessor<K, T> processor) {
		super(pageable);
		this.processor = processor;
	}

	@Override
	public Pageable<K, T> process(K start, long count) {
		return processor.process(start, count);
	}
}
