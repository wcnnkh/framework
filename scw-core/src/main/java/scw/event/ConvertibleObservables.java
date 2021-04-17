package scw.event;

import scw.convert.Converter;
import scw.util.Combiner;

public abstract class ConvertibleObservables<O, T> extends AbstractObservable<T> implements Converter<O, T>{
	private final Observables<O> observables;
	private final ObservableSyncListener<O, T> listener = new ObservableSyncListener<O, T>(this, true, this);
	
	public ConvertibleObservables(boolean concurrent, Combiner<O>combiner) {
		this.observables = new Observables<O>(concurrent, combiner) {
			@Override
			public void onEvent(ChangeEvent<O> event) {
				super.onEvent(event);
				listener.onEvent(event);
			}
		};
	}

	public void addObservable(Observable<O> observable) {
		this.observables.addObservable(observable);
	}

	public T forceGet() {
		O o = observables.forceGet();
		return convert(o);
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<T>> eventListener) {
		return observables.registerListener(exists, new ObservableSyncListener<O, T>(this, false, eventListener));
	}
}
