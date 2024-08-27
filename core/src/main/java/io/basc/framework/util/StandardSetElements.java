package io.basc.framework.util;

import java.util.Set;

import lombok.NonNull;

public class StandardSetElements<E, W extends Set<E>> extends StandardCollectionElements<E, W>
		implements SetElementsWrapper<E, W> {
	private static final long serialVersionUID = 1L;

	public StandardSetElements(@NonNull W source) {
		super(source);
	}

}
