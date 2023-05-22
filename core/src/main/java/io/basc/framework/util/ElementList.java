package io.basc.framework.util;

import java.util.Collections;
import java.util.List;

public class ElementList<E> extends ListWrapper<E> implements Elements<E> {
	public static final ElementList<?> EMPTY = new ElementList<>(Collections.emptyList());

	private static final long serialVersionUID = 1L;

	public ElementList(List<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Elements<E> cacheable() {
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
	public Elements<E> reverse() {
		return Elements.of(() -> CollectionUtils.getIterator(wrappedTarget, true));
	}

	@Override
	public ElementList<E> toList() {
		return this;
	}
	
	@Override
	public boolean isSingleton() {
		return wrappedTarget.size() == 1;
	}
}
