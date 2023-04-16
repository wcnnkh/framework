package io.basc.framework.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ElementCollection<E> extends CollectionWrapper<E, Collection<E>> implements Elements<E> {
	private static final long serialVersionUID = 1L;

	public ElementCollection(Collection<E> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public List<E> toList() {
		if (wrappedTarget instanceof List) {
			return Collections.unmodifiableList((List<E>) wrappedTarget);
		}
		return Elements.super.toList();
	}

	@Override
	public Set<E> toSet() {
		if (wrappedTarget instanceof Set) {
			return Collections.unmodifiableSet((Set<E>) wrappedTarget);
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

}
