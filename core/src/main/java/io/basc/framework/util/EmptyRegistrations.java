package io.basc.framework.util;

public class EmptyRegistrations<R extends Registration> implements Registrations<R> {
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