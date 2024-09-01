package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.function.ConsumeProcessor;

@FunctionalInterface
public interface Registrations<R extends Registration> extends Registration {

	Elements<R> getElements();

	@Override
	default boolean isInvalid() {
		return getElements().allMatch(Registration::isInvalid);
	}

	@Override
	default void deregister() throws RegistrationException {
		ConsumeProcessor.consumeAll(getElements().reverse(), Registration::deregister);
	}
}
