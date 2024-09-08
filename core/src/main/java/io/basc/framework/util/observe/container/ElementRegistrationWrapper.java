package io.basc.framework.util.observe.container;

import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.register.PayloadRegistrationWrapper;

public interface ElementRegistrationWrapper<E, W extends ElementRegistration<E>>
		extends ElementRegistration<E>, PayloadRegistrationWrapper<E, W> {

	@Override
	default E getPayload() {
		return getSource().getPayload();
	}

	@Override
	default ElementRegistration<E> and(Registration registration) {
		return ElementRegistration.super.and(registration);
	}

	@Override
	default E setPayload(E payload) {
		return getSource().setPayload(payload);
	}
}
