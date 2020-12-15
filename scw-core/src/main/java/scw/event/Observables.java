package scw.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import scw.core.utils.CollectionUtils;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.util.CollectionFactory;

public abstract class Observables<T> extends
		AbstractObservable<T> {
	private final AtomicReference<BasicEventDispatcher<ChangeEvent<T>>> existsDispatcher = new AtomicReference<BasicEventDispatcher<ChangeEvent<T>>>();
	private final AtomicReference<BasicEventDispatcher<ChangeEvent<T>>> notExistsDispatcher = new AtomicReference<BasicEventDispatcher<ChangeEvent<T>>>();
	private final List<ObservableRegistion> observables;

	private final boolean concurrent;

	public Observables(boolean concurrent) {
		this.concurrent = concurrent;
		this.observables = CollectionFactory.createArrayList(concurrent);
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void addObservable(Observable<T> observable) {
		this.observables.add(new ObservableRegistion(observable));
	}

	public T forceGet() {
		List<T> list;
		if (CollectionUtils.isEmpty(observables)) {
			list = Collections.emptyList();
		} else {
			list = new ArrayList<T>(observables.size());
			for (ObservableRegistion observable : observables) {
				list.add(observable.getObservable().forceGet());
			}
		}
		return merge(list);
	}

	protected abstract T merge(List<T> list);

	private EventRegistration registerListener(
			EventListener<ChangeEvent<T>> eventListener,
			AtomicReference<BasicEventDispatcher<ChangeEvent<T>>> dispatcherReference) {
		BasicEventDispatcher<ChangeEvent<T>> dispatcher = dispatcherReference
				.get();
		if (dispatcher == null) {
			dispatcher = new DefaultBasicEventDispatcher<ChangeEvent<T>>(
					isConcurrent());
			while (dispatcherReference.compareAndSet(null, dispatcher)) {
				break;
			}
			dispatcher = dispatcherReference.get();
		}
		return dispatcher.registerListener(eventListener);
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<T>> eventListener) {
		EventRegistration eventRegistration = registerListener(eventListener,
				exists ? existsDispatcher : notExistsDispatcher);
		for (ObservableRegistion observable : observables) {
			observable.register();
		}
		return eventRegistration;
	}

	private final class ObservableRegistion {
		private final ObservableItem existItem;
		private final ObservableItem notExistItem;
		private final Observable<T> observable;

		public ObservableRegistion(Observable<T> observable) {
			this.existItem = new ObservableItem(observable, true);
			this.notExistItem = new ObservableItem(observable, false);
			this.observable = observable;
			register();
		}

		public void register() {
			if (existsDispatcher.get() != null) {
				existItem.register();
			}

			if (notExistsDispatcher.get() != null) {
				notExistItem.register();
			}
		}

		public Observable<T> getObservable() {
			return observable;
		}
	}

	private final class ObservableItem implements
			EventListener<ChangeEvent<T>> {
		private final AtomicBoolean registered = new AtomicBoolean();
		private Observable<T> observable;
		private boolean exists;

		public ObservableItem(Observable<T> observable, boolean exists) {
			this.observable = observable;
			this.exists = exists;
		}

		public boolean register() {
			if (registered.get()) {
				return false;
			}

			if (registered.compareAndSet(false, true)) {
				if (exists) {
					observable.registerListener(true, this);
				} else {
					observable.registerListener(false, this);
				}
				return true;
			}
			return false;
		}

		public void onEvent(ChangeEvent<T> event) {
			BasicEventDispatcher<ChangeEvent<T>> dispatcher;
			if (exists) {
				dispatcher = Observables.this.existsDispatcher
						.get();
			} else{
				dispatcher = Observables.this.notExistsDispatcher
						.get();
			}

			ChangeEvent<T> eventToUse = new ChangeEvent<T>(
					event.getEventType(), forceGet());
			dispatcher.publishEvent(eventToUse);
		}
	}
}
