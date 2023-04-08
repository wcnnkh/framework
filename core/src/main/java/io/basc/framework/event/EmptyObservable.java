package io.basc.framework.event;

import io.basc.framework.util.Registration;

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
