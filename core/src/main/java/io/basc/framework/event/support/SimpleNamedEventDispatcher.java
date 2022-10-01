package io.basc.framework.event.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.NamedEventDispatcher;

/**
 * 这是一个同步的基于命名的事件分发器
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <T>
 */
public class SimpleNamedEventDispatcher<K, T extends Event> implements NamedEventDispatcher<K, T> {
	private static final int INITIAL_CAPACITY = Integer.getInteger("io.basc.framework.event.map.initial_capacity", 8);
	private volatile Map<K, EventDispatcher<T>> namedEventListenerMap;

	protected EventDispatcher<T> createEventDispatcher(K name) {
		return new SimpleEventDispatcher<T>();
	}

	public EventRegistration registerListener(K name, EventListener<T> eventListener) {
		synchronized (this) {
			if (namedEventListenerMap == null) {
				namedEventListenerMap = new HashMap<>(INITIAL_CAPACITY);
			}

			EventDispatcher<T> eventDispatcher = namedEventListenerMap.get(name);
			if (eventDispatcher == null) {
				eventDispatcher = createEventDispatcher(name);
				namedEventListenerMap.put(name, eventDispatcher);
			}
			return eventDispatcher.registerListener(eventListener);
		}
	}

	public void publishEvent(K name, T event) {
		synchronized (this) {
			if (namedEventListenerMap == null) {
				return;
			}

			publishEvent(name, event, namedEventListenerMap);
		}
	}

	protected void publishEvent(K name, T event, Map<K, EventDispatcher<T>> dispatcherMap) {
		EventDispatcher<T> dispatcher = dispatcherMap.get(name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
	}
}
