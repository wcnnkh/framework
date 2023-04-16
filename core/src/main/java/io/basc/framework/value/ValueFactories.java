package io.basc.framework.value;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;

public class ValueFactories<K, F extends ValueFactory<K>> extends ConfigurableServices<F> implements ValueFactory<K> {

	@Override
	public Value get(K key) {
		for (F factory : this) {
			if (factory == null || factory == this) {
				continue;
			}

			Value value = factory.get(key);
			if (value != null && value.isPresent()) {
				return value;
			}
		}
		return Value.EMPTY;
	}

	@Override
	public Registration register(F element, int weight) throws RegistrationException {
		Registration registration = super.register(element, weight);
		if (registration.isEmpty()) {
			return registration;
		}

		if (element instanceof DynamicValueFactory) {
			registration = registration
					.and(((DynamicValueFactory<?>) element).getKeyEventRegistry().registerListener((event) -> {
						getElementEventDispatcher()
								.publishEvent(new ChangeEvent<>(ChangeType.UPDATE, Elements.singleton(element)));
					}));
		}
		return registration;
	}
}