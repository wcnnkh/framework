package run.soeasy.framework.core.exchange;

import java.util.List;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;

@FunctionalInterface
public interface Registrations<R extends Registration> extends Registration, Listable<R> {
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
		if (hasElements()) {
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
		return hasElements() ? false : getElements().anyMatch((e) -> e.isCancellable());
	}

	@Override
	default boolean isCancelled() {
		return hasElements() ? false : getElements().allMatch((e) -> e.isCancelled());
	}
}
