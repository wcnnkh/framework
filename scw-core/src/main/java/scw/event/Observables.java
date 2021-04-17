package scw.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.util.CollectionFactory;
import scw.util.Combiner;

public class Observables<T> extends AbstractObservable<T> {
	private final AtomicReference<BasicEventDispatcher<ChangeEvent<T>>> existsDispatcher = new AtomicReference<BasicEventDispatcher<ChangeEvent<T>>>();
	private final AtomicReference<BasicEventDispatcher<ChangeEvent<T>>> notExistsDispatcher = new AtomicReference<BasicEventDispatcher<ChangeEvent<T>>>();
	private final Set<ObservableRegistion> registions;
	private final Combiner<T> combiner;
	
	private final boolean concurrent;

	public Observables(boolean concurrent, Combiner<T> combiner) {
		this.concurrent = concurrent;
		this.combiner = combiner;
		this.registions = CollectionFactory.createSet(concurrent);
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public boolean addObservable(Observable<T> observable) {
		if(this.registions.add(new ObservableRegistion(observable))){
			ChangeEvent<T> event = new ChangeEvent<T>(EventType.CREATE, forceGet());
			onEvent(event);
			onEvent(event, false);
			onEvent(event, true);
			observable.getRegistry().registerListener(new ObservableSyncListener<T, T>(this, true, this));
			return true;
		}
		return false;
	}
	
	public List<Observable<T>> getObservables(){
		if(CollectionUtils.isEmpty(registions)){
			return Collections.emptyList();
		}
		
		List<Observable<T>> observables = new ArrayList<Observable<T>>(this.registions.size());
		for (ObservableRegistion registion : this.registions) {
			observables.add(registion.getObservable());
		}
		
		//排序,优先级降序排列，那么一般情况下进行合并后面的会覆盖前面的
		observables.sort(OrderComparator.INSTANCE);
		return observables;
	}
	
	public T forceGet() {
		List<Observable<T>> observables = getObservables();
		List<T> list;
		if (CollectionUtils.isEmpty(observables)) {
			list = Collections.emptyList();
		} else {
			list = new ArrayList<T>(observables.size());
			for(Observable<T> observable : observables){
				list.add(observable.forceGet());
			}
		}
		return combiner.combine(list);
	}

	private EventRegistration registerListener(
			EventListener<ChangeEvent<T>> eventListener,
			AtomicReference<BasicEventDispatcher<ChangeEvent<T>>> dispatcherReference) {
		BasicEventDispatcher<ChangeEvent<T>> dispatcher = dispatcherReference
				.get();
		if (dispatcher == null) {
			dispatcher = new DefaultBasicEventDispatcher<ChangeEvent<T>>(
					isConcurrent());
			while (dispatcherReference.get() == null && dispatcherReference.compareAndSet(null, dispatcher)) {
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
		for (ObservableRegistion observable : registions) {
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
		
		@Override
		public int hashCode() {
			return observable.hashCode();
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if(obj == null){
				return false;
			}
			
			if(obj instanceof Observables.ObservableRegistion){
				return ((Observables.ObservableRegistion) obj).observable == this.observable;
			}
			
			return false;
		}
	}
	
	private void onEvent(ChangeEvent<T> event, boolean exists){
		BasicEventDispatcher<ChangeEvent<T>> dispatcher;
		if (exists) {
			dispatcher = Observables.this.existsDispatcher.get();
		} else {
			dispatcher = Observables.this.notExistsDispatcher.get();
		}
		
		if(dispatcher == null){
			return ;
		}
		dispatcher.publishEvent(event);
	}

	private final class ObservableItem implements EventListener<ChangeEvent<T>> {
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
			Observables.this.onEvent(new ChangeEvent<T>(event, forceGet()), exists);
		}
	}
}
