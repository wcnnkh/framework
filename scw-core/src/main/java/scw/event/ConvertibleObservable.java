package scw.event;

import scw.convert.Converter;

public class ConvertibleObservable<O, T> extends AbstractObservable<T> {
	private final Observable<O> observable;
	private final Converter<O, T> converter;

	public ConvertibleObservable(Observable<O> observable,
			Converter<O, T> converter) {
		this.observable = observable;
		this.converter = converter;
		observable.registerListener(new EventListener<ChangeEvent<O>>() {

			@Override
			public void onEvent(ChangeEvent<O> event) {
				ConvertibleObservable.this.publishEvent(new ChangeEvent<T>(
						event.getEventType(), forceGet()));
			}
		});
	}

	public Observable<O> getOriginObservable() {
		return observable;
	}

	public T forceGet() {
		return converter.convert(observable.get());
	}
}
