package io.basc.framework.event;

import java.util.Iterator;

import io.basc.framework.util.registry.Registration;

@FunctionalInterface
public interface EventListener<T> extends java.util.EventListener {
	void onEvent(T event);

	default Registration registerTo(Iterator<? extends EventRegistry<T>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}

		Registration registration = Registration.EMPTY;
		while (registries.hasNext()) {
			EventRegistry<T> registry = registries.next();
			if (registry == null) {
				continue;
			}
			registration = registration.and(registry.registerListener(this));
		}
		return registration;
	}

	default Registration registerTo(Iterable<? extends EventRegistry<T>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}

		return registerTo(registries.iterator());
	}

	default <K> Registration registerTo(K name, Iterator<? extends NamedEventRegistry<K, T>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}

		Registration registration = Registration.EMPTY;
		while (registries.hasNext()) {
			NamedEventRegistry<K, T> registry = registries.next();
			if (registry == null) {
				continue;
			}

			registration = registration.and(registry.registerListener(name, this));
		}
		return registration;
	}

	default <K> Registration registerTo(K name, Iterable<? extends NamedEventRegistry<K, T>> registries) {
		if (registries == null) {
			return Registration.EMPTY;
		}
		return registerTo(name, registries.iterator());
	}
}
