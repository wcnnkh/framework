package io.basc.framework.value.observe;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.observe.value.ValueChangeEvent;
import io.basc.framework.util.Registration;

class NotSupportedObservable<T> implements Observable<T> {
	private final T source;

	public NotSupportedObservable(T source) {
		this.source = source;
	}

	@Override
	public T orElse(T other) {
		return source == null ? other : source;
	}

	@Override
	public Registration registerListener(EventListener<ValueChangeEvent<T>> eventListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}
}
