package io.basc.framework.value;

import java.util.Set;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.factory.Configurable;
import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;

public class PropertyFactories extends ValueFactories<String, PropertyFactory>
		implements PropertyFactory, Configurable {
	private final BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher;

	public PropertyFactories() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public PropertyFactories(BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher) {
		this.keyEventDispatcher = keyEventDispatcher;
		setServiceClass(PropertyFactory.class);
	}

	@Override
	public Value get(String key) {
		for (PropertyFactory factory : this) {
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
	public boolean containsKey(String key) {
		for (PropertyFactory factory : this) {
			if (factory == null || factory == this) {
				continue;
			}

			if (factory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> stream().flatMap((e) -> e.keys().stream()).distinct());
	}

	@Override
	public Registration register(PropertyFactory element, int weight) throws RegistrationException {
		Registration registration = super.register(element, weight);
		if (registration.isEmpty()) {
			return registration;
		}

		Set<String> registerKeys = element.keys().toSet();
		ElementSet<String> changeKeys = new ElementSet<>(registerKeys);
		keyEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.CREATE, changeKeys));
		return registration
				.and(() -> keyEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.DELETE, changeKeys)));
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<String>>> getKeyEventRegistry() {
		return keyEventDispatcher;
	}
}
