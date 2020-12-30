package scw.event;

import scw.event.support.EmptyBasicEventDispatcher;

public class NotSupportedObservable<T> implements Observable<T>{
	private final T source;
	
	public NotSupportedObservable(T source){
		this.source = source;
	}
	
	public EventRegistration registerListener(
			EventListener<ChangeEvent<T>> eventListener) {
		return EventRegistration.EMPTY;
	}

	public T get() {
		return source;
	}

	public T forceGet() {
		return source;
	}

	public boolean unregister() {
		return false;
	}

	public boolean isRegistered() {
		return false;
	}

	public boolean register() {
		return false;
	}

	public boolean register(boolean exists) {
		return false;
	}

	private BasicEventRegistry<ChangeEvent<T>> registry;
	public BasicEventRegistry<ChangeEvent<T>> getRegistry() {
		if(registry == null){
			registry = new EmptyBasicEventDispatcher<ChangeEvent<T>>();
		}
		return registry;
	}

	public EventRegistration registerListener(boolean exists,
			EventListener<ChangeEvent<T>> eventListener) {
		return EventRegistration.EMPTY;
	}

}
