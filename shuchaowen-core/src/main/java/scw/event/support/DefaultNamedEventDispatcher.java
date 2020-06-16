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

public class DefaultNamedEventDispatcher<T extends Event> extends DefaultEventDispatcher<T>
		implements NamedEventDispatcher<T> {
	private final CompatibleMap<String, BasicEventDispatcher<T>> namedEventListenerMap;

	public DefaultNamedEventDispatcher(boolean concurrent) {
		super(concurrent);
		Map<String, BasicEventDispatcher<T>> namedEventListenerMap = concurrent
				? new ConcurrentHashMap<String, BasicEventDispatcher<T>>()
				: new HashMap<String, BasicEventDispatcher<T>>();
		this.namedEventListenerMap = CompatibleUtils.getMapCompatible().wrapper(namedEventListenerMap);
	}

	public void unregister(String name) {
		namedEventListenerMap.remove(name);
	}

	public EventRegistration registerListener(String name, EventListener<T> eventListener) {
		BasicEventDispatcher<T> eventDispatcher = namedEventListenerMap.get(name);
		if (eventDispatcher == null) {
			eventDispatcher = new DefaultBasicEventDispatcher<T>(isConcurrent());
			BasicEventDispatcher<T> dispatcher = namedEventListenerMap.putIfAbsent(name, eventDispatcher);
			if (dispatcher != null) {
				eventDispatcher = dispatcher;
			}
		}

		return eventDispatcher.registerListener(eventListener);
	}

	public void publishEvent(String name, T event) {
		BasicEventDispatcher<T> dispatcher = namedEventListenerMap.get(name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
