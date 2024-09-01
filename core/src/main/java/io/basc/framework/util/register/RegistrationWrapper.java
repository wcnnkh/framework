package io.basc.framework.util.register;

import io.basc.framework.util.Wrapper;

public interface RegistrationWrapper<W extends Registration> extends Registration, Wrapper<W> {
	@Override
	default void deregister() throws RegistrationException {
		getSource().deregister();
	}

	@Override
	default Registration and(Registration registration) {
		return getSource().and(registration);
	}

	@Override
	default boolean isInvalid() {
		return getSource().isInvalid();
	}

}
