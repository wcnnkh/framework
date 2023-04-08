package io.basc.framework.util.page;

import java.util.function.Function;

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
	public Pageables<K, T> shared() {
		return Pageables.super.shared();
	}
}
