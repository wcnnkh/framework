package scw.event.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.compatible.CompatibleUtils;
import scw.compatible.map.CompatibleMap;
import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;

public class DefaultNamedEventDispatcher<T extends Event> implements NamedEventDispatcher<T> {
	private final CompatibleMap<Object, BasicEventDispatcher<T>> namedEventListenerMap;
	private boolean concurrent;

	public DefaultNamedEventDispatcher(boolean concurrent) {
		this.concurrent = concurrent;
		Map<Object, BasicEventDispatcher<T>> namedEventListenerMap = concurrent
				? new ConcurrentHashMap<Object, BasicEventDispatcher<T>>(8)
				: new HashMap<Object, BasicEventDispatcher<T>>(8);
		this.namedEventListenerMap = CompatibleUtils.getMapCompatible().wrapper(namedEventListenerMap);
	}

	public final boolean isConcurrent() {
		return concurrent;
	}

	protected BasicEventDispatcher<T> createBasicEventDispatcher(Object name) {
		return new DefaultBasicEventDispatcher<T>(isConcurrent());
	}

	public EventRegistration registerListener(Object name, EventListener<T> eventListener) {
		BasicEventDispatcher<T> eventDispatcher = namedEventListenerMap.get(name);
		if (eventDispatcher == null) {
			eventDispatcher = createBasicEventDispatcher(name);
			BasicEventDispatcher<T> dispatcher = namedEventListenerMap.putIfAbsent(name, eventDispatcher);
			if (dispatcher != null) {
				eventDispatcher = dispatcher;
			}
		}

		return eventDispatcher.registerListener(eventListener);
	}

	public void publishEvent(Object name, T event) {
		BasicEventDispatcher<T> dispatcher = namedEventListenerMap.get(name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
