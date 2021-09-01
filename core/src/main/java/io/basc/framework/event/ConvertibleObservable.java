package io.basc.framework.event;

import io.basc.framework.convert.Converter;

public class ConvertibleObservable<O, T> extends AbstractObservable<T> implements AutoCloseable {
	private final Observable<O> observable;
	private final Converter<O, T> converter;
	private final EventRegistration eventRegistration;

	public ConvertibleObservable(Observable<O> observable,
			Converter<O, T> converter) {
		this.observable = observable;
		this.converter = converter;
		this.eventRegistration = observable.registerListener(new EventListener<ChangeEvent<O>>() {

			@Override
			public void onEvent(ChangeEvent<O> event) {
				ConvertibleObservable.this.publishEvent(new ChangeEvent<T>(
						event.getEventType(), forceGet()));
			}
		});
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
		return converter.convert(observable.get());
	}
}
