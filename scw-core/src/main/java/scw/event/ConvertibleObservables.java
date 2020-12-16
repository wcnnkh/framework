package scw.event;

import java.util.List;

import scw.core.Converter;

public abstract class ConvertibleObservables<O, T> extends
		AbstractObservable<T> implements Converter<O, T> {
	private final Observables<O> observables;
	private final ObservableSyncListener<O, T> listener = new ObservableSyncListener<O, T>(this, true, this);

	public ConvertibleObservables(boolean concurrent) {
		this.observables = new Observables<O>(concurrent) {
			protected O merge(List<O> list) {
				return ConvertibleObservables.this.merge(list);
			};

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

	protected abstract O merge(List<O> list);

	public T forceGet() {
		O o = observables.forceGet();
		return convert(o);
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<T>> eventListener) {
		return observables.registerListener(exists, new ObservableSyncListener<O, T>(this, false, eventListener));
	}
}
