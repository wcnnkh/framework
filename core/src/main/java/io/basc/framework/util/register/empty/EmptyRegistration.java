package io.basc.framework.util.register.empty;

import io.basc.framework.util.register.Registration;

public class EmptyRegistration implements Registration {

	@Override
	public boolean isInvalid() {
		return true;
	}

	@Override
	public void deregister() {
	}

	@Override
	public Registration and(Registration registration) {
		return registration == null ? this : registration;
	}

}
