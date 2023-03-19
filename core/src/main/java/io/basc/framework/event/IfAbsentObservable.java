package io.basc.framework.event;

import java.util.function.Supplier;

import io.basc.framework.util.Registration;

class IfAbsentObservable<T> extends AbstractObservable<T> {
	private final Observable<T> source;
	private final Supplier<? extends T> other;

	public IfAbsentObservable(Observable<T> source, Supplier<? extends T> other) {
		this.source = source;
		this.other = other;
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
		return this.source.registerListener(eventListener);
	}

	@Override
	protected T getValue() {
		return source.orElse(other.get());
	}
}