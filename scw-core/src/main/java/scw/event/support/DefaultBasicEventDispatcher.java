package scw.event.support;

import java.util.Collection;

import scw.core.Assert;
import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.AlreadyExistsException;
import scw.util.CollectionFactory;

public class DefaultBasicEventDispatcher<T extends Event> implements BasicEventDispatcher<T> {
	private final Collection<EventRegistrationInternal> eventListeners;
	private final boolean concurrent;

	public DefaultBasicEventDispatcher(boolean concurrent) {
		this.concurrent = concurrent;
		this.eventListeners = CollectionFactory.createSet(concurrent);
	}

	public final boolean isConcurrent() {
		return concurrent;
	}

	public EventRegistration registerListener(EventListener<T> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");

		EventRegistration eventRegistration = new EventRegistrationInternal(eventListener);
		if (eventListeners.contains(eventRegistration)) {
			throw new AlreadyExistsException(eventRegistration.toString());
		}

		eventListeners.add(new EventRegistrationInternal(eventListener));
		return eventRegistration;
	}

	public void publishEvent(T event) {
		Assert.requiredArgument(event != null, "event");

		for (EventRegistrationInternal registrationInternal : eventListeners) {
			registrationInternal.getEventListener().onEvent(event);
		}
	}

	private class EventRegistrationInternal implements EventRegistration {
		private final EventListener<T> eventListener;

		public EventRegistrationInternal(EventListener<T> eventListener) {
			this.eventListener = eventListener;
		}

		public void unregister() {
			eventListeners.remove(this);
		}

		public EventListener<T> getEventListener() {
			return eventListener;
		}
	}
}
