package scw.event.method;

import java.util.Arrays;
import java.util.List;

import scw.event.EventRegistration;

public class MultiEventRegistration implements EventRegistration {
	private List<EventRegistration> list;

	public MultiEventRegistration(EventRegistration ...registrations) {
		this(Arrays.asList(registrations));
	}
	
	public MultiEventRegistration(List<EventRegistration> list) {
		this.list = list;
	}

	public void unregister() {
		if (list != null) {
			for (EventRegistration registration : list) {
				if(registration == null){
					continue;
				}
				
				registration.unregister();
			}
		}
	}
}
