package io.basc.framework.util;

import java.util.List;
import java.util.stream.Collectors;

public class ListElements<E> extends CollectionElements<E, List<E>> implements ListElementsWrapper<E, List<E>> {
	private static final long serialVersionUID = 1L;

	public ListElements(Elements<E> elements) {
		super(elements, Collectors.toList());
	}
}
