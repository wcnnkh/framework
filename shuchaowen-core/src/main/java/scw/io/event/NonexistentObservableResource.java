package scw.io.event;

import scw.event.EventRegistration;
import scw.event.support.EmptyEventRegistration;

public class NonexistentObservableResource<T> extends ObservableResource<T> {

	public NonexistentObservableResource() {
		super(null);
	}

	@Override
	public EventRegistration registerListener(ObservableResourceEventListener<T> eventListener) {
		return new EmptyEventRegistration();
	}
}
