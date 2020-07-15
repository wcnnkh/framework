package scw.io.event;

import scw.event.EventRegistration;

public abstract class ObservableResource<T> {
	private final T resource;

	public ObservableResource(T resource) {
		this.resource = resource;
	}

	public T getResource() {
		return resource;
	}

	public abstract EventRegistration registerListener(ObservableResourceEventListener<T> eventListener);
}
