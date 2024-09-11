package io.basc.framework.util.observe.register.container;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.register.PayloadRegistration;

public interface ElementRegistration<V> extends PayloadRegistration<V> {

	V setPayload(V payload);

	@Override
	default ElementRegistration<V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		return new StandardElementRegistrationWrappe<>(this, Elements.singleton(registration));
	}
}
