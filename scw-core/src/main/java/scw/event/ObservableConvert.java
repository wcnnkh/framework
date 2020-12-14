package scw.event;

import scw.core.Converter;

public class ObservableConvert<O, T> extends AbstractObservable<T> {
	private final Converter<O, T> converter;
	private final Observable<O> observable;

	public ObservableConvert(Observable<O> observable, Converter<O, T> converter) {
		this.observable = observable;
		this.converter = converter;
	}

	public T forceGet() {
		return converter.convert(observable.forceGet());
	}

	@Override
	public boolean register() {
		return observable.register();
	}

	@Override
	public EventRegistration registerListener(
			final EventListener<ObservableEvent<T>> eventListener) {
		return observable
				.registerListener(new EventListener<ObservableEvent<O>>() {

					public void onEvent(ObservableEvent<O> event) {
						eventListener.onEvent(new ObservableEvent<T>(event
								.getEventType(), forceGet()));
					}
				});
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ObservableEvent<T>> eventListener) {
		return observable.registerListener(exists,
				new EventListener<ObservableEvent<O>>() {

					public void onEvent(ObservableEvent<O> event) {
						eventListener.onEvent(new ObservableEvent<T>(event
								.getEventType(), forceGet()));
					}
				});
	}
}
