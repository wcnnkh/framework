package scw.io.event;

import java.util.Collection;

import scw.core.utils.CollectionUtils;
import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.MultiEventRegistration;
import scw.io.Resource;

public abstract class AbstractObservableResources<T> extends
		AbstractObservable<T> {
	public abstract Collection<Resource> getResources();

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<T>> eventListener) {
		
		Collection<Resource> resources = getResources();
		if (CollectionUtils.isEmpty(resources)) {
			return EventRegistration.EMPTY;
		}

		EventRegistration[] eventRegistrations = new EventRegistration[resources
				.size()];
		int i = 0;
		for (Resource resource : resources) {
			if (exists && !resource.exists()) {
				continue;
			}
			
			eventRegistrations[i++] = resource.registerListener(new EventListener<ResourceEvent>() {
						public void onEvent(ResourceEvent event) {
							eventListener.onEvent(new ChangeEvent<T>(event, forceGet()));
						}
					});
		}
		return new MultiEventRegistration(eventRegistrations);
	}
}
