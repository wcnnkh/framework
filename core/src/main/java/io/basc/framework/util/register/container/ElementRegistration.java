package io.basc.framework.util.register.container;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.register.LifecycleRegistration;
import io.basc.framework.util.register.PayloadRegistration;

public interface ElementRegistration<V> extends PayloadRegistration<V>, LifecycleRegistration {

	V setPayload(V payload);

	@Override
	default ElementRegistration<V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		return new StandardElementRegistrationWrappe<>(this, Elements.singleton(registration));
	}
}
