package io.basc.framework.util.register;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.element.Elements;

public interface BrowseableRegistry<T, R extends PayloadRegistration<T>> extends Registry<T, R>, Registrations<R> {
	default void deregister(T element) {
		for (PayloadRegistration<T> registration : getRegistrations()) {
			if (ObjectUtils.equals(registration.getPayload(), element)) {
				registration.deregister();
			}
		}
	}

	@Override
	default Elements<T> getServices() {
		return getRegistrations().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload());
	}

	/**
	 * 期望支持批量注册
	 */
	@Override
	default R register(T element) throws RegistrationException {
		Registrations<R> registrations = registers(Elements.singleton(element));
		return registrations.getRegistrations().first();
	}

	@Override
	Registrations<R> registers(Iterable<? extends T> elements) throws RegistrationException;
}
