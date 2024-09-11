package io.basc.framework.util.observe.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.observe.Registrations;

public interface BrowseableRegistry<T, R extends PayloadRegistration<T>> extends Registry<T> {
	Registrations<R> getRegistrations();

	default void deregister(T element) {
		for (PayloadRegistration<T> registration : getRegistrations().getElements()) {
			if (ObjectUtils.equals(registration.getPayload(), element)) {
				registration.cancel();
			}
		}
	}

	@Override
	default Elements<T> getElements() {
		return getRegistrations().getElements().filter((e) -> !e.isCancelled()).map((e) -> e.getPayload());
	}

	@Override
	R register(T element) throws RegistrationException;
}
