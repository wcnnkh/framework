package io.basc.framework.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class DisposableRegistration implements Registration {
	private Registration registration;
	private final AtomicBoolean unregistred = new AtomicBoolean(false);

	public DisposableRegistration(Registration registration) {
		this.registration = registration;
	}

	@Override
	public void unregister() {
		if (unregistred.compareAndSet(false, true)) {
			registration.unregister();
		}
	}

	public boolean isUnregistred() {
		return unregistred.get();
	}

	@Override
	public Registration disposable() {
		return this;
	}

	@Override
	public Registration and(Registration registration) {
		this.registration = this.registration.and(registration);
		return this;
	}
}
