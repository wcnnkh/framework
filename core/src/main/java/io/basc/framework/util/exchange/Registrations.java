package io.basc.framework.util.exchange;

import java.util.List;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.Listable;

@FunctionalInterface
public interface Registrations<R extends Registration> extends Registration, Listable<R> {
	@FunctionalInterface
	public static interface RegistrationsWrapper<R extends Registration, W extends Registrations<R>>
			extends Registrations<R>, RegistrationWrapper<W>, ListableWrapper<R, W> {
		@Override
		default boolean cancel() {
			return getSource().cancel();
		}

		@Override
		default boolean isCancellable() {
			return getSource().isCancellable();
		}

		@Override
		default boolean isCancelled() {
			return getSource().isCancelled();
		}
	}

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

	public static <E extends Registration> Registrations<E> forElements(Elements<E> elements) {
		if (elements == null) {
			return empty();
		}
		return () -> elements;
	}

	public static <E extends Registration> Registrations<E> forList(List<? extends E> list) {
		if (list == null) {
			return empty();
		}

		return forElements(Elements.of(list));
	}

	@Override
	default boolean cancel() {
		if (isEmpty()) {
			return false;
		}

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
		return isEmpty() ? false : getElements().anyMatch((e) -> e.isCancellable());
	}

	@Override
	default boolean isCancelled() {
		return isEmpty() ? false : getElements().allMatch((e) -> e.isCancelled());
	}
}
