package scw.event.support;

import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.util.CollectionFactory;
import scw.util.GenericMap;

public class DefaultNamedEventDispatcher<K, T extends Event> implements
		NamedEventDispatcher<K, T> {
	private volatile GenericMap<K, BasicEventDispatcher<T>> namedEventListenerMap;
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

	public GenericMap<K, BasicEventDispatcher<T>> getNamedEventListenerMap() {
		if (namedEventListenerMap == null) {
			synchronized (this) {
				if (namedEventListenerMap == null) {
					this.namedEventListenerMap = CollectionFactory
							.createHashMap(concurrent, initialCapacity);
				}
			}
		}
		return namedEventListenerMap;
	}

	protected BasicEventDispatcher<T> createBasicEventDispatcher(K name) {
		return new DefaultBasicEventDispatcher<T>(isConcurrent());
	}

	public EventRegistration registerListener(K name,
			EventListener<T> eventListener) {
		BasicEventDispatcher<T> eventDispatcher = getNamedEventListenerMap()
				.get(name);
		if (eventDispatcher == null) {
			eventDispatcher = createBasicEventDispatcher(name);
			BasicEventDispatcher<T> dispatcher = getNamedEventListenerMap()
					.putIfAbsent(name, eventDispatcher);
			if (dispatcher != null) {
				eventDispatcher = dispatcher;
			}
		}

		return eventDispatcher.registerListener(eventListener);
	}

	public void publishEvent(K name, T event) {
		BasicEventDispatcher<T> dispatcher = getNamedEventListenerMap().get(
				name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
