package io.basc.framework.util;

import java.util.Set;

public class SetWrapper<E> extends CollectionWrapper<E, Set<E>> implements Set<E> {
	private static final long serialVersionUID = 1L;

	public SetWrapper(Set<E> wrappedTarget) {
		super(wrappedTarget);
	}
}
