package io.basc.framework.event;

import java.util.function.Function;

import io.basc.framework.util.Registration;

class ConvertObservable<S, T> extends AbstractObservable<T> {
	private final Observable<S> source;
	private final Function<? super S, ? extends T> mapper;

	ConvertObservable(Observable<S> source, Function<? super S, ? extends T> mapper) {
		this.source = source;
		this.mapper = mapper;
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<T>> eventListener) {
		return source.registerListener((e) -> eventListener.onEvent(new ObservableChangeEvent<>(e, mapper)));
	}

	@Override
	protected T getValue() {
		S value = source.orElse(null);
		return mapper.apply(value);
	}

}