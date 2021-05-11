package scw.event;

import scw.convert.Converter;

public class ConvertibleObservable<O, T> extends AbstractObservable<T> {
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
	protected void finalize() throws Throwable {
		eventRegistration.unregister();
		super.finalize();
	}

	public Observable<O> getOriginObservable() {
		return observable;
	}

	public T forceGet() {
		return converter.convert(observable.get());
	}
}
