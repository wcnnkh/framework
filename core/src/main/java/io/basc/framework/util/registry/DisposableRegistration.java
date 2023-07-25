package io.basc.framework.util.registry;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.util.Assert;

public class DisposableRegistration implements Registration {
	private Registration registration;
	private final AtomicBoolean unregistred = new AtomicBoolean(false);

	public DisposableRegistration(Registration registration) {
		Assert.requiredArgument(registration != null, "registration");
		this.registration = registration;
	}

	@Override
	public void unregister() {
		if (!isEmpty() && unregistred.compareAndSet(false, true)) {
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

	public static Registration of(Registration registration) {
		if (registration instanceof DisposableRegistration) {
			return (DisposableRegistration) registration;
		}
		return new DisposableRegistration(registration);
	}
}
