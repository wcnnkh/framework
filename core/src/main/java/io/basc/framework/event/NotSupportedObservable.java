package io.basc.framework.event;

import io.basc.framework.util.Registration;

public class NotSupportedObservable<T> implements Observable<T> {
	private final T source;

	public NotSupportedObservable(T source) {
		this.source = source;
	}

	@Override
	public T orElse(T other) {
		return source == null ? other : source;
	}

	@Override
	public Registration registerListener(EventListener<ChangeEvent<T>> eventListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}
}
