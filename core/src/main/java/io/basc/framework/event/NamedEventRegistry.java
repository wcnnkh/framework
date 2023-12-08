package io.basc.framework.event;

import io.basc.framework.util.Registration;

public interface NamedEventRegistry<K, T> {
	Registration registerListener(K name, EventListener<T> eventListener) throws EventRegistrationException;
}
