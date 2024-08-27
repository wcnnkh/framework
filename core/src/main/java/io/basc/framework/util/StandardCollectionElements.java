package io.basc.framework.util;

import java.util.Collection;

import lombok.NonNull;

public class StandardCollectionElements<E, W extends Collection<E>> extends StandardIterableElements<E, W>
		implements CollectionElementsWrapper<E, W> {
	private static final long serialVersionUID = 1L;

	public StandardCollectionElements(@NonNull W source) {
		super(source);
	}

}
