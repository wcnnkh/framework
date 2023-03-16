package io.basc.framework.event;

import java.util.function.Predicate;

import io.basc.framework.util.Registration;

class FilterObservable<T> extends AbstractObservable<T> {
	private final Observable<T> source;
	private final Predicate<? super T> predicate;

	FilterObservable(Observable<T> source, Predicate<? super T> predicate) {
		this.source = source;
		this.predicate = predicate;
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
		return this.source.registerListener(eventListener);
	}

	@Override
	protected T getValue() {
		T value = source.orElse(null);
		if (value == null) {
			return null;
		}
		return predicate.test(value) ? value : null;
	}
}