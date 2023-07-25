package io.basc.framework.event.observe;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.util.registry.Registration;

public class EmptyObservable<T> implements Observable<T> {
	public static EmptyObservable<Object> INSTANCE = new EmptyObservable<>();

	@Override
	public T orElse(T other) {
		return other;
	}

	@Override
	public Registration registerListener(EventListener<ChangeEvent<T>> eventListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}

}
