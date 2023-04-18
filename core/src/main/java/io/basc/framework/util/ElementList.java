package io.basc.framework.util;

import java.util.List;

public class ElementList<E> extends ListWrapper<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;

	public ElementList(List<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public ElementList<E> toList() {
		return this;
	}

	@Override
	public Elements<E> reverse() {
		return Elements.of(() -> CollectionUtils.getIterator(wrappedTarget, true));
	}

	@Override
	public long count() {
		return wrappedTarget.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedTarget.isEmpty();
	}

	@Override
	public Elements<E> cacheable() {
		return this;
	}
}
