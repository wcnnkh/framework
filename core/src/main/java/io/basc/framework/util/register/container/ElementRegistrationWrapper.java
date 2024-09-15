package io.basc.framework.util.register.container;

import io.basc.framework.util.Registration;
import io.basc.framework.util.register.LifecycleRegistrationWrapper;
import io.basc.framework.util.register.PayloadRegistrationWrapper;

public interface ElementRegistrationWrapper<E, W extends ElementRegistration<E>>
		extends ElementRegistration<E>, PayloadRegistrationWrapper<E, W>, LifecycleRegistrationWrapper<W> {

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

	@Override
	default void start() {
		getSource().start();
	}

	@Override
	default void stop() {
		getSource().stop();
	}

	@Override
	default boolean isRunning() {
		return getSource().isRunning();
	}
}
