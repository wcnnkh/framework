package io.basc.framework.util.register;

import io.basc.framework.util.RegistrationWrapper;

public interface LifecycleRegistrationWrapper<W extends LifecycleRegistration>
		extends LifecycleRegistration, RegistrationWrapper<W> {
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
