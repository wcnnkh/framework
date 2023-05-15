package io.basc.framework.util;

import java.io.Serializable;

public class EmptyServiceLoader<S> implements ServiceLoader<S>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final EmptyServiceLoader<?> EMPTY = new EmptyServiceLoader<>();

	public void reload() {
	}

	@Override
	public Elements<S> getServices() {
		return Elements.empty();
	}
}
