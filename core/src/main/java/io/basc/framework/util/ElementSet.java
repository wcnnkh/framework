package io.basc.framework.util;

import java.util.Collections;
import java.util.Set;

public class ElementSet<E> extends SetWrapper<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;

	public ElementSet(Set<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Set<E> toSet() {
		return Collections.unmodifiableSet(wrappedTarget);
	}

	@Override
	public Elements<E> clone() {
		return new ElementSet<>(CollectionFactory.clone(wrappedTarget));
	}
}
