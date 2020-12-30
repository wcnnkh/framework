package scw.event;

import scw.convert.Converter;

public abstract class ConvertibleObservable<O, T> extends AbstractObservable<T>
		implements Converter<O, T> {
	private final Observable<O> observable;

	public ConvertibleObservable(Observable<O> observable) {
		this.observable = observable;
		observable.getRegistry().registerListener(
				new ObservableSyncListener<O, T>(this, true, this));
	}

	public Observable<O> getOriginObservable() {
		return observable;
	}

	public T forceGet() {
		return convert(observable.forceGet());
	}

	public EventRegistration registerListener(boolean exists,
			EventListener<ChangeEvent<T>> eventListener) {
		return observable.registerListener(exists, new ObservableSyncListener<O, T>(this,
				false, eventListener));
	}
}
