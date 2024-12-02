package io.basc.framework.util;

@FunctionalInterface
public interface Registrations<R extends Registration> extends Registration, Listable<R> {

	public static class EmptyRegistrations<R extends Registration> implements Registrations<R> {
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

	public static final Registrations<?> EMPTY_REGISTRATIONS = new EmptyRegistrations<>();

	@SuppressWarnings("unchecked")
	public static <E extends Registration> Registrations<E> empty() {
		return (Registrations<E>) EMPTY_REGISTRATIONS;
	}

	public static <E extends Registration> Registrations<E> of(Elements<E> elements) {
		if (elements == null) {
			return empty();
		}
		return () -> elements;
	}

	@Override
	default boolean cancel() {
		for (R registration : getElements()) {
			if (registration.isCancellable()) {
				if (!registration.cancel()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	default boolean isCancellable() {
		return getElements().anyMatch((e) -> e.isCancellable());
	}

	@Override
	default boolean isCancelled() {
		return getElements().allMatch((e) -> e.isCancelled());
	}
}
