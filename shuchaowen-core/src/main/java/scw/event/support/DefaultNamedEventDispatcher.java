package scw.event.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEvent;
import scw.event.NamedEventListener;

public class DefaultNamedEventDispatcher extends DefaultEventDispatcher {
	private Map<String, EventDispatcher> namedEventListenerMap = new ConcurrentHashMap<String, EventDispatcher>();

	@SuppressWarnings("unchecked")
	@Override
	public EventRegistration registerListener(EventListener<? extends Event> eventListener) {
		if (eventListener instanceof NamedEventListener) {
			NamedEventListener<NamedEvent> listener = (NamedEventListener<NamedEvent>) eventListener;
			EventDispatcher eventDispatcher = namedEventListenerMap.get(listener.getName());
			if (eventDispatcher == null) {
				eventDispatcher = new DefaultEventDispatcher();
				EventDispatcher dispatcher = namedEventListenerMap.putIfAbsent(listener.getName(), eventDispatcher);
				if (dispatcher != null) {
					eventDispatcher = dispatcher;
				}
			}

			EventRegistration registration = eventDispatcher.registerListener(listener);
			return new NamedEventRegistration(registration);
		}

		return super.registerListener(eventListener);
	}

	@Override
	public void publishEvent(Event event) {
		if (event instanceof NamedEvent) {
			NamedEvent namedEvent = (NamedEvent) event;
			EventDispatcher dispatcher = namedEventListenerMap.get(namedEvent.getName());
			if (dispatcher == null) {
				return;
			}

			dispatcher.publishEvent(namedEvent);
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
}
