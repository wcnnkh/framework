package io.basc.framework.util;

import java.util.List;

import lombok.NonNull;

public class StandardListElements<E, W extends List<E>> extends StandardCollectionElements<E, W>
		implements ListElementsWrapper<E, W> {
	private static final long serialVersionUID = 1L;

	public StandardListElements(@NonNull W source) {
		super(source);
	}
}
