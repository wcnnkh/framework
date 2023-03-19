package io.basc.framework.event;

import io.basc.framework.util.Registration;

public class NotSupportedObservable<T> extends AbstractObservable<T> {
	private final T source;

	public NotSupportedObservable(T source) {
		this.source = source;
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
		return Registration.EMPTY;
	}

	@Override
	public T getValue() {
		return source;
	}
}
