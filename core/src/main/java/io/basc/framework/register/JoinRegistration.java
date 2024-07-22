package io.basc.framework.register;

import java.util.Arrays;

import io.basc.framework.util.function.ConsumeProcessor;
import lombok.NonNull;

public class JoinRegistration implements Registration {
	private static final int JOIN_MAX_LENGTH = Integer.max(2,
			Integer.getInteger(JoinRegistration.class.getName() + ".maxLength", 256));
	@NonNull
	private final Registration[] registrations;

	public JoinRegistration(@NonNull Registration... registrations) {
		this.registrations = registrations;
	}

	@Override
	public boolean isInvalid() {
		for (Registration registration : registrations) {
			if (!registration.isInvalid()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void unregister() throws RegistrationException {
		ConsumeProcessor.consumeAll(Arrays.asList(registrations), (e) -> e.unregister());
	}

	@Override
	public JoinRegistration and(Registration registration) {
		if (registration == null || registration == Registration.EMPTY) {
			return this;
		}

		if (registrations.length == JOIN_MAX_LENGTH) {
			return new JoinRegistration(this, registration);
		} else {
			Registration[] registrations = Arrays.copyOf(this.registrations, this.registrations.length + 1);
			registrations[this.registrations.length] = registration;
			return new JoinRegistration(registrations);
		}
	}
}
