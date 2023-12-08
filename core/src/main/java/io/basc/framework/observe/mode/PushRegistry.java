package io.basc.framework.observe.mode;

import io.basc.framework.observe.Push;
import io.basc.framework.observe.register.ElementRegistration;
import io.basc.framework.observe.register.ElementRegistry;
import io.basc.framework.observe.register.RegistryEvent;
import io.basc.framework.observe.register.RegistryEventType;

public class PushRegistry<E extends Push<?>> extends ElementRegistry<E> {
	@Override
	public ElementRegistration<E> register(E element) {
		ElementRegistration<E> elementRegistration = super.register(element);
		if (element != this) {
			elementRegistration = elementRegistration.and(element.registerListener(
					(e) -> publishEvent(new RegistryEvent<>(this, RegistryEventType.UPDATE, element))));
		}
		return elementRegistration;
	}
}
