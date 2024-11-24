package io.basc.framework.util;

public interface ListableWrapper<E, W extends Listable<E>> extends Listable<E>, Wrapper<W> {
	@Override
	default Elements<E> getElements() {
		return getSource().getElements();
	}

	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}
}
