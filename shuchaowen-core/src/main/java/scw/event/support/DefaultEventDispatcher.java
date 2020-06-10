package scw.event.support;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.CopyOnWriteArraySet;

import scw.core.Assert;
import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.lang.AlreadyExistsException;

public class DefaultEventDispatcher implements EventDispatcher {
	private final Collection<EventRegistrationInternal> eventListeners;
	private final boolean concurrent;

	public DefaultEventDispatcher(boolean concurrent){
		this.concurrent = concurrent;
		this.eventListeners = concurrent? new CopyOnWriteArraySet<EventRegistrationInternal>():new LinkedHashSet<DefaultEventDispatcher.EventRegistrationInternal>();
	}
	
	public final boolean isConcurrent() {
		return concurrent;
	}

	public EventRegistration registerListener(EventListener<? extends Event> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");

		EventRegistration eventRegistration = new EventRegistrationInternal(eventListener);
		if (eventListeners.contains(eventRegistration)) {
			throw new AlreadyExistsException(eventRegistration.toString());
		}

		eventListeners.add(new EventRegistrationInternal(eventListener));
		return eventRegistration;
	}

	public void publishEvent(Event event) {
		Assert.requiredArgument(event != null, "event");

		for (EventRegistrationInternal registrationInternal : eventListeners) {
			registrationInternal.getEventListener().onEvent(event);
		}
	}

	private class EventRegistrationInternal implements EventRegistration {
		private final EventListener<? extends Event> eventListener;

		public EventRegistrationInternal(EventListener<? extends Event> eventListener) {
			this.eventListener = eventListener;
		}

		public void unregister() {
			eventListeners.remove(this);
		}

		@SuppressWarnings("unchecked")
		public EventListener<Event> getEventListener() {
			return (EventListener<Event>) eventListener;
		}
	}
}
