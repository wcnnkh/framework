package io.basc.framework.util.register.container;

import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;

public interface ElementRegistration<V> extends PayloadRegistration<V> {
	@Override
	default V getPayload() {
		return getValue();
	}

	V getValue();

	V setValue(V value);

	@Override
	default ElementRegistration<V> and(Registration registration) {
		if (registration == null || registration == EMPTY) {
			return this;
		}

		return new CombinableElementRegistration<>(this, registration);
	}
}
