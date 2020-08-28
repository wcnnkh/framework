package scw.io.event;

import scw.event.EventRegistration;

public class NonexistentObservableResource<T> extends ObservableResource<T> {

	public NonexistentObservableResource() {
		super(null);
	}

	@Override
	public EventRegistration registerListener(ObservableResourceEventListener<T> eventListener, boolean isExist) {
		return EventRegistration.EMPTY;
	}
}
