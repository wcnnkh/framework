package io.basc.framework.event;

public class NotSupportedObservable<T> implements Observable<T> {
	private final T source;

	public NotSupportedObservable(T source) {
		this.source = source;
	}

	public T get() {
		return source;
	}

	public EventRegistration registerListener(EventListener<ChangeEvent<T>> eventListener) {
		return EventRegistration.EMPTY;
	}
}
