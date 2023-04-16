package io.basc.framework.event.support;

import java.util.Arrays;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.DynamicElementRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.DefaultElementRegistry;
import io.basc.framework.util.ElementRegistration;
import io.basc.framework.util.ElementRegistry;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;
import io.basc.framework.util.Registrations;

public class DefaultDynamicElementRegistry<E> implements DynamicElementRegistry<E> {
	private final ElementRegistry<E> elementRegistry;
	private final BroadcastEventDispatcher<ChangeEvent<Elements<E>>> elementEventDispatcher;

	public DefaultDynamicElementRegistry() {
		this(new DefaultElementRegistry<>());
	}

	public DefaultDynamicElementRegistry(ElementRegistry<E> elementRegistry) {
		this(elementRegistry, new StandardBroadcastEventDispatcher<>());
	}

	public DefaultDynamicElementRegistry(ElementRegistry<E> elementRegistry,
			BroadcastEventDispatcher<ChangeEvent<Elements<E>>> elementEventDispatcher) {
		Assert.requiredArgument(elementRegistry != null, "elementRegistry");
		Assert.requiredArgument(elementEventDispatcher != null, "elementEventDispatcher");
		this.elementRegistry = elementRegistry;
		this.elementEventDispatcher = elementEventDispatcher;
	}

	public final BroadcastEventDispatcher<ChangeEvent<Elements<E>>> getElementEventDispatcher() {
		return elementEventDispatcher;
	}

	@Override
	public final Registration register(E element) throws RegistrationException {
		Assert.requiredArgument(element != null, "element");
		return registers(Arrays.asList(element));
	}

	@Override
	public Elements<E> getElements() {
		return elementRegistry.getElements();
	}

	private Registration registerUpdateListener(E element) {
		if (element instanceof DynamicElementRegistry) {
			return ((DynamicElementRegistry<?>) element).getElementEventRegistry().registerListener((event) -> {
				getElementEventDispatcher().publishEvent(
						new ChangeEvent<>(event.getCreateTime(), ChangeType.UPDATE, Elements.singleton(element)));
			});
		}
		return Registration.EMPTY;
	}

	@Override
	public Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		Registrations<ElementRegistration<E>> registrations = elementRegistry.registers(elements);
		if (registrations.isEmpty()) {
			return registrations;
		}

		Elements<E> changeElements = registrations.getElements().map((e) -> e.getElement());
		for (E element : changeElements) {
			Registration registration = registerUpdateListener(element);
			if (registration.isEmpty()) {
				continue;
			}
			registrations = registrations.and(registration);
		}

		elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.CREATE, changeElements));
		return registrations.and(() -> {
			elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.DELETE, changeElements));
		});
	}

	@Override
	public Registrations<ElementRegistration<E>> clear() throws RegistrationException {
		Registrations<ElementRegistration<E>> registrations = elementRegistry.clear();
		if (registrations.isEmpty()) {
			return registrations;
		}

		Elements<E> changeElements = registrations.getElements().map((e) -> e.getElement());
		elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.DELETE, changeElements));
		return registrations
				.and(() -> elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.CREATE, changeElements)));
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<E>>> getElementEventRegistry() {
		return elementEventDispatcher;
	}
}
