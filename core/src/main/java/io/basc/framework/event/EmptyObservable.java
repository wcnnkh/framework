package io.basc.framework.event;

import io.basc.framework.util.Registration;

public class EmptyObservable<T> extends AbstractObservable<T> {

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
		return Registration.EMPTY;
	}

	@Override
	public T getValue() {
		return null;
	}

}
