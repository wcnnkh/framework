package io.basc.framework.util.observe;

import io.basc.framework.util.Document;
import io.basc.framework.util.Elements;

@FunctionalInterface
public interface Registrations<R extends Registration> extends Registration, Document<R> {
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
