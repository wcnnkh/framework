package scw.event.method;

import java.util.Arrays;
import java.util.Collection;

import scw.event.EventRegistration;

public class MultiEventRegistration implements EventRegistration {
	private Collection<EventRegistration> eventRegistrations;

	public MultiEventRegistration(EventRegistration... registrations) {
		this(Arrays.asList(registrations));
	}

	public MultiEventRegistration(Collection<EventRegistration> eventRegistrations) {
		this.eventRegistrations = eventRegistrations;
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
