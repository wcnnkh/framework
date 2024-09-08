package io.basc.framework.util.observe;

import io.basc.framework.util.Document;

public interface Registrations<R extends Registration> extends Registration, Document<R> {
	@Override
	default boolean isCancellable() {
		return getElements().anyMatch((e) -> e.isCancellable());
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
	default boolean isCancelled() {
		return getElements().allMatch((e) -> e.isCancelled());
	}
}
