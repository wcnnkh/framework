package scw.event;

import scw.core.Converter;

public abstract class AbstractObservableConvert<O, T> extends
		AbstractObservable<T> implements Converter<O, T> {
	private final Observable<O> observable;

	public AbstractObservableConvert(Observable<O> observable) {
		this.observable = observable;
	}
	
	public Observable<O> getOriginObservable(){
		return observable;
	}

	public T forceGet() {
		return convert(observable.forceGet());
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<T>> eventListener) {
		return observable.registerListener(exists,
				new EventListener<ChangeEvent<O>>() {

					public void onEvent(ChangeEvent<O> event) {
						eventListener.onEvent(new ChangeEvent<T>(event
								.getEventType(), forceGet()));
					}
				});
	}
}
