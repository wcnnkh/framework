package scw.io.event;

import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.io.Resource;

public abstract class AbstractObservableResource<T> extends
		AbstractObservable<T> {

	public abstract Resource getResource();

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<T>> eventListener) {
		Resource resource = getResource();
		if (exists && !resource.exists()) {
			return EventRegistration.EMPTY;
		}

		return resource.getEventDispatcher().registerListener(
				new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ChangeEvent<T>(event, forceGet()));
					}
				});
	}
}
