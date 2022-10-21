package io.basc.framework.event;

import io.basc.framework.util.Registration;

public interface NamedEventRegistry<K, T extends Event> {
	Registration registerListener(K name, EventListener<T> eventListener);
}
