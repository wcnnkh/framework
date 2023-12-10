package io.basc.framework.observe.register;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.Observable;

public class ObservableRegistry<E extends Observable<?>> extends ElementRegistry<E> {
	@Override
	public ElementRegistration<E> register(E element) {
		ElementRegistration<E> elementRegistration = super.register(element);
		if (element != this) {
			elementRegistration = elementRegistration.and(element
					.registerListener((e) -> publishEvent(new RegistryEvent<>(this, ChangeType.UPDATE, element))));
		}
		return elementRegistration;
	}
}
