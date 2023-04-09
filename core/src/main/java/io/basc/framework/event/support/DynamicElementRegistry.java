package io.basc.framework.event.support;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ElementRegistration;
import io.basc.framework.util.ElementRegistry;
import io.basc.framework.util.Elements;

public class DynamicElementRegistry<E> extends ElementRegistry<E> {
	private final BroadcastEventDispatcher<ChangeEvent<Elements<E>>> elementEventDispatcher;

	public DynamicElementRegistry() {
		this(new CopyOnWriteArraySet<>(), new StandardBroadcastEventDispatcher<>());
	}

	public DynamicElementRegistry(Collection<E> elements,
			BroadcastEventDispatcher<ChangeEvent<Elements<E>>> elementEventDispatcher) {
		super(elements);
		Assert.requiredArgument(elementEventDispatcher != null, "elementEventDispatcher");
		this.elementEventDispatcher = elementEventDispatcher;
	}

	public final BroadcastEventDispatcher<ChangeEvent<Elements<E>>> getElementEventDispatcher() {
		return elementEventDispatcher;
	}

	@Override
	public ElementRegistration<E> registers(Iterable<? extends E> elements) {
		ElementRegistration<E> registration = super.registers(elements);
		if (registration.isEmpty()) {
			return registration;
		}

		elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.CREATE, registration.getElements()));
		return registration.and(() -> elementEventDispatcher
				.publishEvent(new ChangeEvent<>(ChangeType.DELETE, registration.getElements())));
	}

	public ElementRegistration<E> clear() {
		ElementRegistration<E> registration = super.clear();
		if (registration.isEmpty()) {
			return registration;
		}
		elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.DELETE, registration.getElements()));
		return registration.and(() -> elementEventDispatcher
				.publishEvent(new ChangeEvent<>(ChangeType.CREATE, registration.getElements())));
	}
}
