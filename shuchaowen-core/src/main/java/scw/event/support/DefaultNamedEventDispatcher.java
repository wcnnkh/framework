package scw.event.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Assert;
import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEvent;
import scw.event.NamedEventDispatcher;
import scw.event.NamedEventListener;

public class DefaultNamedEventDispatcher extends DefaultEventDispatcher implements NamedEventDispatcher {
	private Map<String, EventDispatcher> namedEventListenerMap = new ConcurrentHashMap<String, EventDispatcher>();

	@SuppressWarnings("unchecked")
	@Override
	public EventRegistration registerListener(EventListener<? extends Event> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");
		
		if (eventListener instanceof NamedEventListener) {
			return registerListener((NamedEventListener<Event>) eventListener);
		}

		return super.registerListener(eventListener);
	}

	@Override
	public void publishEvent(Event event) {
		Assert.requiredArgument(event != null, "event");
		
		if (event instanceof NamedEvent) {
			publishEvent((NamedEvent) event);
			return;
		}
		super.publishEvent(event);
	}

	private final class NamedEventRegistration implements EventRegistration {
		private final EventRegistration registration;

		public NamedEventRegistration(EventRegistration registration) {
			this.registration = registration;
		}

		public void unregister() {
			registration.unregister();
		}
	}

	public void unregister(String name) {
		namedEventListenerMap.remove(name);
	}

	public EventRegistration registerListener(NamedEventListener<? extends Event> eventListener) {
		return registerListener(eventListener.getName(), eventListener);
	}

	public void publishEvent(NamedEvent event) {
		publishEvent(event.getName(), event);
	}

	public EventRegistration registerListener(String name, EventListener<? extends Event> eventListener) {
		EventDispatcher eventDispatcher = namedEventListenerMap.get(name);
		if (eventDispatcher == null) {
			eventDispatcher = new DefaultEventDispatcher();
			EventDispatcher dispatcher = namedEventListenerMap.putIfAbsent(name, eventDispatcher);
			if (dispatcher != null) {
				eventDispatcher = dispatcher;
			}
		}

		EventRegistration registration = eventDispatcher.registerListener(eventListener);
		return new NamedEventRegistration(registration);
	}

	public void publishEvent(String name, Event event) {
		EventDispatcher dispatcher = namedEventListenerMap.get(name);
		if (dispatcher == null) {
			return;
		}

		dispatcher.publishEvent(event);
		return;
	}
}
