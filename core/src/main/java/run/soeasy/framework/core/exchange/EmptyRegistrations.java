package run.soeasy.framework.core.exchange;

import run.soeasy.framework.core.collection.Elements;

public class EmptyRegistrations<R extends Registration> implements Registrations<R> {
	static final EmptyRegistrations<?> EMPTY = new EmptyRegistrations<>();

	@SuppressWarnings("unchecked")
	public static <E extends Registration> Registrations<E> empty() {
		return (Registrations<E>) EMPTY;
	}

	@Override
	public Elements<R> getElements() {
		return Elements.empty();
	}
}