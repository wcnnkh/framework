package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;

public interface BrowseableRegistry<T, R extends PayloadRegistration<T>> extends Registry<T> {
	Registrations<R> getRegistrations();

	default void deregister(T element) {
		for (PayloadRegistration<T> registration : getRegistrations().getElements()) {
			if (ObjectUtils.equals(registration.getPayload(), element)) {
				registration.deregister();
			}
		}
	}

	@Override
	default Elements<T> getElements() {
		return getRegistrations().getElements().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload());
	}

	/**
	 * 期望支持批量注册
	 */
	@Override
	default R register(T element) throws RegistrationException {
		Registrations<R> registrations = registers(Elements.singleton(element));
		return registrations.getElements().first();
	}

	@Override
	Registrations<R> registers(Iterable<? extends T> elements) throws RegistrationException;
}
