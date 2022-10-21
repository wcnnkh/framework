package io.basc.framework.event;

import io.basc.framework.util.Registration;

public interface EventRegistry<T extends Event> {
	Registration registerListener(EventListener<T> eventListener);
}
