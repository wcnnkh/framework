package io.basc.framework.util;

public class EmptyServiceLoader<S> extends EmptyElements<S> implements ServiceLoader<S> {
	private static final long serialVersionUID = 1L;
	public static final EmptyServiceLoader<?> EMPTY = new EmptyServiceLoader<>();

	public void reload() {
	}
}
