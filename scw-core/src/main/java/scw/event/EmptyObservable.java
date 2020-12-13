package scw.event;

public final class EmptyObservable<T> extends AbstractObservable<T> {

	public T forceGet() {
		return null;
	}

	public EventRegistration registerListener(boolean exists,
			EventListener<ObservableEvent<T>> eventListener) {
		return EventRegistration.EMPTY;
	}
}
