package io.basc.framework.util;

import java.util.Collections;
import java.util.List;

public class ElementList<E> extends ListWrapper<E> implements Elements<E> {
	private static final long serialVersionUID = 1L;

	public ElementList(List<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public List<E> toList() {
		return Collections.unmodifiableList(wrappedTarget);
	}

	@Override
	public Elements<E> reverse() {
		return Elements.of(() -> CollectionUtils.getIterator(wrappedTarget, true));
	}
}
