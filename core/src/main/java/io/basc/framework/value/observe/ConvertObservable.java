package io.basc.framework.value.observe;

import java.util.function.Function;

import io.basc.framework.event.EventListener;
import io.basc.framework.observe.value.ValueChangeEvent;
import io.basc.framework.util.Registration;

class ConvertObservable<S, T> implements Observable<T> {
	private final Function<? super S, ? extends T> mapper;
	private final Observable<S> source;

	ConvertObservable(Observable<S> source, Function<? super S, ? extends T> mapper) {
		this.source = source;
		this.mapper = mapper;
	}

	@Override
	public T orElse(T other) {
		S value = source.orElse(null);
		return value == null ? other : mapper.apply(value);
	}

	@Override
	public Registration registerListener(EventListener<ValueChangeEvent<T>> eventListener) {
		return source.registerListener((e) -> {
			S source = e.getSource();
			S oldSource = e.getOldSource();
			T target = source == null ? null : mapper.apply(source);
			T oldTarget = oldSource == null ? null : mapper.apply(oldSource);
			eventListener.onEvent(new ValueChangeEvent<>(e, oldTarget, target));
		});
	}

}