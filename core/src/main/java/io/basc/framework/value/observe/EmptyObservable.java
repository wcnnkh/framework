package io.basc.framework.value.observe;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.observe.value.ValueChangeEvent;
import io.basc.framework.util.Registration;

class EmptyObservable<T> implements Observable<T> {
	public static EmptyObservable<Object> INSTANCE = new EmptyObservable<>();

	@Override
	public T orElse(T other) {
		return other;
	}

	@Override
	public Registration registerListener(EventListener<ValueChangeEvent<T>> eventListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}

}
