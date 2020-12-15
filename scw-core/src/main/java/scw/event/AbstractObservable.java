package scw.event;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractObservable<T> implements Observable<T>, EventListener<ChangeEvent<T>>{
	private volatile T value;
	private volatile EventRegistration eventRegistration;
	private volatile AtomicBoolean registered = new AtomicBoolean(false);
	private volatile AtomicBoolean firstGet = new AtomicBoolean(false);
	
	public void unregister() {
		if(eventRegistration != null && registered.compareAndSet(true, false)){
			eventRegistration.unregister();
			eventRegistration = null;
		}
	}

	public T get() {
		if(value == null && !firstGet.get() && firstGet.compareAndSet(false, true)){
			set(forceGet());
		}
		return value;
	}
	
	protected void set(T value) {
		this.value = value;
	}

	public boolean isRegistered() {
		return registered.get();
	}
	
	public boolean register() {
		return register(true);
	}

	public boolean register(boolean exists) {
		if(isRegistered()){
			return false;
		}
		
		if(registered.compareAndSet(false, true)){
			eventRegistration = registerListener(exists, this);
			return true;
		}
		return false;
	}
	
	public EventRegistration registerListener(
			EventListener<ChangeEvent<T>> eventListener) {
		return registerListener(true, eventListener);
	}
	
	public void onEvent(ChangeEvent<T> event) {
		set(forceGet());
	}
}
