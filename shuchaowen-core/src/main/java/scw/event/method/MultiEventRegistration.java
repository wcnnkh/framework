package scw.event.method;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import scw.event.EventRegistration;

public class MultiEventRegistration implements EventRegistration {
	private List<EventRegistration> eventRegistrations;

	public MultiEventRegistration(EventRegistration... registrations) {
		this(Arrays.asList(registrations));
	}

	public MultiEventRegistration(Collection<EventRegistration> eventRegistrations) {
		this.eventRegistrations = Arrays.asList(eventRegistrations.toArray(new EventRegistration[0]));
	}

	public void unregister() {
		if (eventRegistrations != null) {
			for (EventRegistration registration : eventRegistrations) {
				if (registration == null) {
					continue;
				}

				registration.unregister();
			}
		}
	}
}
