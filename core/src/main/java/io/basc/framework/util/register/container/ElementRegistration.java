package io.basc.framework.util.register.container;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.ServiceRegistration;
import lombok.NonNull;

public class ElementRegistration<T> extends ServiceRegistration<T> {
	public ElementRegistration(T payload) {
		super(payload);
	}

	protected ElementRegistration(ServiceRegistration<T> serviceRegistration) {
		super(serviceRegistration);
	}

	@Override
	public ElementRegistration<T> and(@NonNull Registration registration) {
		return new ElementRegistration<>(super.and(registration));
	}

	@Override
	public ElementRegistration<T> andAll(@NonNull Elements<? extends Registration> registrations) {
		return new ElementRegistration<>(super.andAll(registrations));
	}
}
