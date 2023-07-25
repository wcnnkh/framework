package io.basc.framework.event.observe;

import java.util.function.Function;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.util.registry.Registration;

class ConvertObservable<S, T> implements Observable<T> {
	private final Observable<S> source;
	private final Function<? super S, ? extends T> mapper;

	ConvertObservable(Observable<S> source, Function<? super S, ? extends T> mapper) {
		this.source = source;
		this.mapper = mapper;
	}

	@Override
	public Registration registerListener(EventListener<ChangeEvent<T>> eventListener) {
		return source.registerListener((e) -> {
			S source = e.getSource();
			T target = mapper.apply(source);
			eventListener.onEvent(new ChangeEvent<>(e, target));
		});
	}

	@Override
	public T orElse(T other) {
		S value = source.orElse(null);
		return value == null ? other : mapper.apply(value);
	}

}