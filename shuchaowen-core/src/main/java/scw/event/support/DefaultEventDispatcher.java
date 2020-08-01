package scw.event.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.compatible.CompatibleUtils;
import scw.compatible.map.CompatibleMap;
import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;

public class DefaultEventDispatcher<T extends Event> extends DefaultBasicEventDispatcher<T>
		implements EventDispatcher<T> {
	private final CompatibleMap<Object, BasicEventDispatcher<T>> namedEventListenerMap;
	private final CompatibleMap<Class<? extends T>, BasicEventDispatcher<T>> typeEventListenerMap;

	public DefaultEventDispatcher(boolean concurrent) {
		super(concurrent);
		Map<Class<? extends T>, BasicEventDispatcher<T>> typeEventListenerMap = concurrent
				? new ConcurrentHashMap<Class<? extends T>, BasicEventDispatcher<T>>()
				: new HashMap<Class<? extends T>, BasicEventDispatcher<T>>();
		this.typeEventListenerMap = CompatibleUtils.getMapCompatible().wrapper(typeEventListenerMap);

		Map<Object, BasicEventDispatcher<T>> namedEventListenerMap = concurrent
				? new ConcurrentHashMap<Object, BasicEventDispatcher<T>>(8)
				: new HashMap<Object, BasicEventDispatcher<T>>(8);
		this.namedEventListenerMap = CompatibleUtils.getMapCompatible().wrapper(namedEventListenerMap);
	}

	public void unregister(Class<? extends T> eventType) {
		typeEventListenerMap.remove(eventType);
	}

	@SuppressWarnings("unchecked")
	public <E extends T> EventRegistration registerListener(Class<E> eventType, EventListener<E> eventListener) {
		BasicEventDispatcher<T> eventDispatcher = typeEventListenerMap.get(eventType);
		if (eventDispatcher == null) {
			eventDispatcher = new DefaultBasicEventDispatcher<T>(isConcurrent());
			BasicEventDispatcher<T> dispatcher = typeEventListenerMap.putIfAbsent(eventType, eventDispatcher);
			if (dispatcher != null) {
				eventDispatcher = dispatcher;
			}
		}

		return eventDispatcher.registerListener((EventListener<T>) eventListener);
	}

	public <E extends T> void publishEvent(Class<E> eventType, E event) {
		BasicEventDispatcher<T> eventDispatcher = typeEventListenerMap.get(eventType);
		if (eventDispatcher != null) {
			eventDispatcher.publishEvent(event);
		}
	}

	public void unregister(Object name) {
		namedEventListenerMap.remove(name);
	}

	public EventRegistration registerListener(Object name, EventListener<T> eventListener) {
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

	public void publishEvent(Object name, T event) {
		BasicEventDispatcher<T> dispatcher = namedEventListenerMap.get(name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
