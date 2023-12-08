package io.basc.framework.event;

import io.basc.framework.util.Registration;

public interface EventRegistry<T> {
	Registration registerListener(EventListener<T> eventListener) throws EventRegistrationException;
}
