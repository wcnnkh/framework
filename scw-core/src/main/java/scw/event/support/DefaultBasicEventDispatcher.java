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
	private volatile Collection<EventRegistrationInternal> eventListeners;
	private final boolean concurrent;
	private final int initialCapacity;
	
	public DefaultBasicEventDispatcher(boolean concurrent) {
		this(concurrent, 8);
	}

	public DefaultBasicEventDispatcher(boolean concurrent, int initialCapacity) {
		this.concurrent = concurrent;
		this.initialCapacity = initialCapacity;
	}

	public Collection<EventRegistrationInternal> getEventListeners() {
		if(eventListeners == null){
			synchronized (this) {
				if(eventListeners == null){
					this.eventListeners = CollectionFactory.createSet(concurrent, initialCapacity);
				}
			}
		}
		return eventListeners;
	}

	public final boolean isConcurrent() {
		return concurrent;
	}

	public EventRegistration registerListener(EventListener<T> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");

		EventRegistration eventRegistration = new EventRegistrationInternal(eventListener);
		if (getEventListeners().contains(eventRegistration)) {
			throw new AlreadyExistsException(eventRegistration.toString());
		}

		getEventListeners().add(new EventRegistrationInternal(eventListener));
		return eventRegistration;
	}

	public void publishEvent(T event) {
		Assert.requiredArgument(event != null, "event");

		for (EventRegistrationInternal registrationInternal : getEventListeners()) {
			registrationInternal.getEventListener().onEvent(event);
		}
	}

	private class EventRegistrationInternal implements EventRegistration {
		private final EventListener<T> eventListener;

		public EventRegistrationInternal(EventListener<T> eventListener) {
			this.eventListener = eventListener;
		}

		public void unregister() {
			getEventListeners().remove(this);
		}

		public EventListener<T> getEventListener() {
			return eventListener;
		}
	}
}
