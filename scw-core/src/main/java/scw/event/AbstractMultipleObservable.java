package scw.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import scw.core.utils.CollectionUtils;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.util.CollectionFactory;

public abstract class AbstractMultipleObservable<T> extends
		AbstractObservable<T> {
	private final AtomicReference<BasicEventDispatcher<ObservableEvent<T>>> dispatcher = new AtomicReference<BasicEventDispatcher<ObservableEvent<T>>>();
	private final AtomicReference<BasicEventDispatcher<ObservableEvent<T>>> existsDispatcher = new AtomicReference<BasicEventDispatcher<ObservableEvent<T>>>();
	private final AtomicReference<BasicEventDispatcher<ObservableEvent<T>>> notExistsDispatcher = new AtomicReference<BasicEventDispatcher<ObservableEvent<T>>>();
	private final List<ObservableRegistion> observables;

	private final boolean concurrent;

	public AbstractMultipleObservable(boolean concurrent) {
		this.concurrent = concurrent;
		this.observables = CollectionFactory.createArrayList(concurrent);
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void addObservable(Observable<T> observable) {
		this.observables.add(new ObservableRegistion(observable));
	}

	public void unregister() {
		if (CollectionUtils.isEmpty(observables)) {
			return;
		}

		for (ObservableRegistion observable : observables) {
			observable.getObservable().unregister();
		}
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

	public boolean isRegistered() {
		if (CollectionUtils.isEmpty(observables)) {
			return false;
		}

		for (ObservableRegistion observable : observables) {
			if (observable.getObservable().isRegistered()) {
				return true;
			}
		}
		return false;
	}

	public boolean register() {
		if (CollectionUtils.isEmpty(observables)) {
			return false;
		}

		for (ObservableRegistion observable : observables) {
			observable.getObservable().register();
		}
		return true;
	}

	public boolean register(boolean exists) {
		if (CollectionUtils.isEmpty(observables)) {
			return false;
		}

		for (ObservableRegistion observable : observables) {
			observable.getObservable().register(exists);
		}
		return true;
	}

	private EventRegistration registerListener(
			EventListener<ObservableEvent<T>> eventListener,
			AtomicReference<BasicEventDispatcher<ObservableEvent<T>>> dispatcherReference) {
		BasicEventDispatcher<ObservableEvent<T>> dispatcher = dispatcherReference
				.get();
		if (dispatcher == null) {
			dispatcher = new DefaultBasicEventDispatcher<ObservableEvent<T>>(
					isConcurrent());
			while (dispatcherReference.compareAndSet(null, dispatcher)) {
				break;
			}
			dispatcher = dispatcherReference.get();
		}
		return dispatcher.registerListener(eventListener);
	}

	public EventRegistration registerListener(
			final EventListener<ObservableEvent<T>> eventListener) {
		EventRegistration eventRegistration = registerListener(eventListener,
				dispatcher);
		for (ObservableRegistion observable : observables) {
			observable.register();
		}
		return eventRegistration;
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ObservableEvent<T>> eventListener) {
		EventRegistration eventRegistration = registerListener(eventListener,
				exists ? existsDispatcher : notExistsDispatcher);
		for (ObservableRegistion observable : observables) {
			observable.register();
		}
		return eventRegistration;
	}

	private final class ObservableRegistion {
		private final ObservableItem item;
		private final ObservableItem existItem;
		private final ObservableItem notExistItem;
		private final Observable<T> observable;

		public ObservableRegistion(Observable<T> observable) {
			this.item = new ObservableItem(observable, 0);
			this.existItem = new ObservableItem(observable, 1);
			this.notExistItem = new ObservableItem(observable, 2);
			this.observable = observable;
			register();
		}

		public void register() {
			if (dispatcher.get() != null) {
				item.register();
			}

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
			EventListener<ObservableEvent<T>> {
		private final AtomicBoolean registered = new AtomicBoolean();
		private Observable<T> observable;
		private int type;// 0, 1, 2

		public ObservableItem(Observable<T> observable, int type) {
			this.observable = observable;
			this.type = type;
		}

		public boolean register() {
			if (registered.get()) {
				return false;
			}

			if (registered.compareAndSet(false, true)) {
				if (type == 0) {
					observable.registerListener(this);
				} else if (type == 1) {
					observable.registerListener(true, this);
				} else if (type == 2) {
					observable.registerListener(false, this);
				}
				return true;
			}
			return false;
		}

		public void onEvent(ObservableEvent<T> event) {
			BasicEventDispatcher<ObservableEvent<T>> dispatcher = null;
			if (type == 0) {
				dispatcher = AbstractMultipleObservable.this.dispatcher.get();
			} else if (type == 1) {
				dispatcher = AbstractMultipleObservable.this.existsDispatcher
						.get();
			} else if (type == 2) {
				dispatcher = AbstractMultipleObservable.this.notExistsDispatcher
						.get();
			}

			if (dispatcher == null) {
				return;
			}

			ObservableEvent<T> eventToUse = new ObservableEvent<T>(
					event.getEventType(), forceGet());
			dispatcher.publishEvent(eventToUse);
		}
	}
}
