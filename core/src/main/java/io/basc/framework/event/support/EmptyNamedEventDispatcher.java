package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.util.Registration;

public class EmptyNamedEventDispatcher<K, T extends Event> implements NamedEventDispatcher<K, T> {
	public Registration registerListener(K name, EventListener<T> eventListener) {
		return Registration.EMPTY;
	}

	public void publishEvent(K name, T event) {
		// ignore
	}
}
