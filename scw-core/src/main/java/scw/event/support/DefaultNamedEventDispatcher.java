package scw.event.support;

import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.util.CollectionFactory;
import scw.util.GenericMap;

public class DefaultNamedEventDispatcher<K, T extends Event> implements NamedEventDispatcher<K, T> {
	private final GenericMap<K, BasicEventDispatcher<T>> namedEventListenerMap;
	private final boolean concurrent;

	public DefaultNamedEventDispatcher(boolean concurrent) {
		this.concurrent = concurrent;
		this.namedEventListenerMap = CollectionFactory.createHashMap(concurrent);
	}

	public final boolean isConcurrent() {
		return concurrent;
	}

	public GenericMap<K, BasicEventDispatcher<T>> getNamedEventListenerMap() {
		return namedEventListenerMap;
	}

	protected BasicEventDispatcher<T> createBasicEventDispatcher(K name) {
		return new DefaultBasicEventDispatcher<T>(isConcurrent());
	}

	public EventRegistration registerListener(K name, EventListener<T> eventListener) {
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

	public void publishEvent(K name, T event) {
		BasicEventDispatcher<T> dispatcher = namedEventListenerMap.get(name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
