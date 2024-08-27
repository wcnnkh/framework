package io.basc.framework.util;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;

public class SetElements<E> extends CollectionElements<E, Set<E>> implements SetElementsWrapper<E, Set<E>> {
	private static final long serialVersionUID = 1L;

	public SetElements(@NonNull Elements<E> elements) {
		super(elements, Collectors.toSet());
	}
}
