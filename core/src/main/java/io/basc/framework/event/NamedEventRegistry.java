package io.basc.framework.event;

import io.basc.framework.register.Registration;

public interface NamedEventRegistry<K, T> {
	Registration registerListener(K name, EventListener<T> eventListener) throws EventRegistrationException;
}
