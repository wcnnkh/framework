package io.basc.framework.event;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Registration;

public class Observables<T> extends AbstractObservable<List<T>> {
	private final List<? extends Observable<T>> sources;

	public Observables(List<? extends Observable<T>> sources) {
		Assert.requiredArgument(!CollectionUtils.isEmpty(sources), "sources");
		this.sources = sources;
	}

	@Override
	public Registration registerListener(EventListener<ObservableChangeEvent<List<T>>> eventListener) {
		Registration registration = Registration.EMPTY;
		int index = 0;
		for (Observable<T> source : sources) {
			registration = registration.and(source.registerListener(new IndexEventListener(index, eventListener)));
		}
		return registration;
	}

	@Override
	public List<T> getValue() {
		List<T> list = new ArrayList<>(sources.size());
		sources.forEach(((e) -> e.ifPresent(list::add)));
		return list;
	}

	private class IndexEventListener implements EventListener<ObservableChangeEvent<T>> {
		private final int index;
		private final EventListener<ObservableChangeEvent<List<T>>> targetEventListener;

		IndexEventListener(int index, EventListener<ObservableChangeEvent<List<T>>> targetEventListener) {
			this.index = index;
			this.targetEventListener = targetEventListener;
		}

		@Override
		public void onEvent(ObservableChangeEvent<T> event) {
			List<T> newValue = new ArrayList<>(sources.size());
			sources.forEach(((e) -> newValue.add(e.orElse(null))));
			List<T> oldValue = new ArrayList<>(newValue);
			oldValue.set(index, event.getOldSource());
			targetEventListener.onEvent(new ObservableChangeEvent<>(event, oldValue, newValue));
		}
	}
}
