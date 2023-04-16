package io.basc.framework.util;

import java.util.function.Predicate;

public class EmptyServiceLoader<S> extends EmptyElements<S> implements ServiceLoader<S> {
	private static final long serialVersionUID = 1L;
	public static final EmptyServiceLoader<?> EMPTY = new EmptyServiceLoader<>();

	public void reload() {
	}

	@Override
	public ServiceLoader<S> filter(Predicate<? super S> predicate) {
		return this;
	}
}
