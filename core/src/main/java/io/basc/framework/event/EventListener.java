package io.basc.framework.event;

import java.util.Iterator;

import io.basc.framework.register.Registration;

@FunctionalInterface
public interface EventListener<E> extends java.util.EventListener {
	void onEvent(E event);

	default Registration registerTo(Iterator<? extends EventRegistry<E>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}

		Registration registration = Registration.EMPTY;
		while (registries.hasNext()) {
			EventRegistry<E> registry = registries.next();
			if (registry == null) {
				continue;
			}
			registration = registration.and(registry.registerListener(this));
		}
		return registration;
	}

	default Registration registerTo(Iterable<? extends EventRegistry<E>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}

		return registerTo(registries.iterator());
	}

	default <K> Registration registerTo(K name, Iterator<? extends NamedEventRegistry<K, E>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}

		Registration registration = Registration.EMPTY;
		while (registries.hasNext()) {
			NamedEventRegistry<K, E> registry = registries.next();
			if (registry == null) {
				continue;
			}

			registration = registration.and(registry.registerListener(name, this));
		}
		return registration;
	}

	default <K> Registration registerTo(K name, Iterable<? extends NamedEventRegistry<K, E>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}
		return registerTo(name, registries.iterator());
	}
}
