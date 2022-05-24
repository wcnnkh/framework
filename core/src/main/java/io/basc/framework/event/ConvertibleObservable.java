package io.basc.framework.event;

import io.basc.framework.util.stream.Processor;

public class ConvertibleObservable<O, T> extends AbstractObservable<T> implements AutoCloseable {
	private final Observable<O> observable;
	private final Processor<O, ? extends T, ? extends RuntimeException> converter;
	private final EventRegistration eventRegistration;

	public ConvertibleObservable(Observable<O> observable,
			Processor<O, ? extends T, ? extends RuntimeException> converter) {
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
		return converter.process(observable.get());
	}
}
