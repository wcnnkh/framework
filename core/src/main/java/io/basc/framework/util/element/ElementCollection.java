package io.basc.framework.util.element;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.collect.CollectionWrapper;

public class ElementCollection<E> extends CollectionWrapper<E, Collection<E>> implements Elements<E> {
	private static final long serialVersionUID = 1L;

	public ElementCollection(Collection<E> wrappedTarget) {
		super(wrappedTarget);
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
	public ElementList<E> toList() {
		if (wrappedTarget instanceof List) {
			return new ElementList<>((List<E>) wrappedTarget);
		}
		return Elements.super.toList();
	}

	@Override
	public ElementSet<E> toSet() {
		if (wrappedTarget instanceof Set) {
			return new ElementSet<>((Set<E>) wrappedTarget);
		}
		return Elements.super.toSet();
	}

	@Override
	public Elements<E> reverse() {
		if (wrappedTarget instanceof List) {
			return Elements.of(() -> CollectionUtils.getIterator((List<E>) wrappedTarget, true));
		}
		return Elements.super.reverse();
	}

	@Override
	public Elements<E> cacheable() {
		return this;
	}

}
