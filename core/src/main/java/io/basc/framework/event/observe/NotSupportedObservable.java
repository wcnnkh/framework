package io.basc.framework.event.observe;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.util.registry.Registration;

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
