package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.Wrapper;

public interface ListableWrapper<E, W extends Listable<E>> extends Listable<E>, Wrapper<W> {
	@Override
	default Elements<E> getElements() {
		return getSource().getElements();
	}

	@Override
	default boolean hasElements() {
		return getSource().hasElements();
	}
}