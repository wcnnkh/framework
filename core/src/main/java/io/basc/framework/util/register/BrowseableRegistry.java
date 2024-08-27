package io.basc.framework.util.register;

import java.util.Iterator;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;

public interface BrowseableRegistry<T, R extends PayloadRegistration<T>> extends Registry<T, R>, Registrations<R> {
	default void deregister(T element) {
		for (PayloadRegistration<T> registration : getRegistrations()) {
			if (ObjectUtils.equals(registration.getPayload(), element)) {
				registration.deregister();
			}
		}
	}

	@Override
	default Iterator<T> iterator() {
		return getRegistrations().filter((e) -> !e.isInvalid()).map((e) -> e.getPayload()).iterator();
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
