package io.basc.framework.util.observe;

import io.basc.framework.util.Elements;

public class EmptyRegistrations<R extends Registration> extends EmptyRegistration implements Registrations<R> {
	private static final EmptyRegistrations<?> EMPTY = new EmptyRegistrations<>();

	@SuppressWarnings("unchecked")
	public static <E extends Registration> Registrations<E> empty() {
		return (Registrations<E>) EMPTY;
	}

	@Override
	public Elements<R> getElements() {
		return Elements.empty();
	}
}
