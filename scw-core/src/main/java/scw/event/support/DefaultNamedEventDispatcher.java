package scw.event.support;

import scw.event.EventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.util.SmartMap;

/**
 * 这是一个同步的基于命名的事件分发器
 * @author shuchaowen
 *
 * @param <K>
 * @param <T>
 */
public class DefaultNamedEventDispatcher<K, T extends Event> implements
		NamedEventDispatcher<K, T> {
	private volatile SmartMap<K, EventDispatcher<T>> namedEventListenerMap;
	private final boolean concurrent;
	private final int initialCapacity;

	public DefaultNamedEventDispatcher(boolean concurrent) {
		this(concurrent, 8);
	}

	public DefaultNamedEventDispatcher(boolean concurrent, int initialCapacity) {
		this.concurrent = concurrent;
		this.initialCapacity = initialCapacity;
	}

	public final boolean isConcurrent() {
		return concurrent;
	}

	public SmartMap<K, EventDispatcher<T>> getNamedEventListenerMap() {
		if (namedEventListenerMap == null) {
			synchronized (this) {
				if (namedEventListenerMap == null) {
					this.namedEventListenerMap = new SmartMap<>(concurrent, initialCapacity);
				}
			}
		}
		return namedEventListenerMap;
	}

	protected EventDispatcher<T> createEventDispatcher(K name) {
		return new DefaultEventDispatcher<T>(isConcurrent());
	}

	public EventRegistration registerListener(K name,
			EventListener<T> eventListener) {
		EventDispatcher<T> eventDispatcher = getNamedEventListenerMap()
				.get(name);
		if (eventDispatcher == null) {
			eventDispatcher = createEventDispatcher(name);
			EventDispatcher<T> dispatcher = getNamedEventListenerMap()
					.putIfAbsent(name, eventDispatcher);
			if (dispatcher != null) {
				eventDispatcher = dispatcher;
			}
		}

		return eventDispatcher.registerListener(eventListener);
	}

	public void publishEvent(K name, T event) {
		EventDispatcher<T> dispatcher = getNamedEventListenerMap().get(
				name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
