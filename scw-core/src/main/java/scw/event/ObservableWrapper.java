package scw.event;

import scw.core.OrderComparator.OrderSourceProvider;


public class ObservableWrapper<T> implements Observable<T>, OrderSourceProvider{
	private final Observable<T> observable;

	public ObservableWrapper(Observable<T> observable) {
		this.observable = observable;
	}

	public EventRegistration registerListener(
			EventListener<ChangeEvent<T>> eventListener) {
		return observable.registerListener(eventListener);
	}

	public T get() {
		return observable.get();
	}

	public T forceGet() {
		return observable.forceGet();
	}

	public boolean unregister() {
		return observable.unregister();
	}

	public boolean isRegistered() {
		return observable.isRegistered();
	}

	public boolean register() {
		return observable.register();
	}
 
	public boolean register(boolean exists) {
		return observable.register(exists);
	}

	public BasicEventRegistry<ChangeEvent<T>> getRegistry() {
		return observable.getRegistry();
	}

	public EventRegistration registerListener(boolean exists,
			EventListener<ChangeEvent<T>> eventListener) {
		return observable.registerListener(exists, eventListener);
	}

	public Object getOrderSource(Object obj) {
		return observable;
	}
}
