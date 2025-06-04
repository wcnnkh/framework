package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

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
