package io.basc.framework.event;

import java.util.function.Function;

public class ConvertibleObservable<O, T> extends AbstractObservable<T> implements AutoCloseable {
	private final Observable<O> observable;
	private final Function<O, ? extends T> converter;
	private final EventRegistration eventRegistration;

	public ConvertibleObservable(Observable<O> observable, Function<O, ? extends T> converter) {
		this.observable = observable;
		this.converter = converter;
		this.eventRegistration = observable.registerListener((event) -> ConvertibleObservable.this
				.publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet())));
	}

	@Override
	public void close() {
		eventRegistration.unregister();
	}

	public Observable<O> getOriginObservable() {
		return observable;
	}

	@Override
	public T forceGet() {
		return converter.apply(observable.get());
	}
}
