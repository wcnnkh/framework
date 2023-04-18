package io.basc.framework.util;

import java.util.Set;

public class ElementSet<E> extends SetWrapper<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;

	public ElementSet(Set<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public ElementSet<E> toSet() {
		return this;
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
