package scw.value.property;

import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.support.StringNamedEventDispatcher;

public abstract class AbstractBasePropertyFactory implements
		BasePropertyFactory {
	private final NamedEventDispatcher<String, PropertyEvent> eventDispatcher;

	public AbstractBasePropertyFactory(boolean concurrent) {
		this.eventDispatcher = new StringNamedEventDispatcher<PropertyEvent>(
				concurrent);
	}

	public EventRegistration registerListener(String key,
			EventListener<PropertyEvent> eventListener) {
		return eventDispatcher.registerListener(key, eventListener);
	}

	public final NamedEventDispatcher<String, PropertyEvent> getEventDispatcher() {
		return eventDispatcher;
	}
}
