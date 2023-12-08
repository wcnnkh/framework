package io.basc.framework.util.element;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.collect.ListWrapper;

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
	public E getUnique() throws NoSuchElementException, NoUniqueElementException {
		if (wrappedTarget.isEmpty()) {
			throw new NoSuchElementException();
		}

		if (wrappedTarget.size() != 1) {
			throw new NoUniqueElementException();
		}
		return wrappedTarget.get(0);
	}

	@Override
	public boolean isEmpty() {
		return wrappedTarget.isEmpty();
	}

	@Override
	public boolean isUnique() {
		return wrappedTarget.size() == 1;
	}

	@Override
	public Elements<E> reverse() {
		return Elements.of(() -> CollectionUtils.getIterator(wrappedTarget, true));
	}

	@Override
	public ElementList<E> toList() {
		return this;
	}
}
