package scw.event;

import scw.util.Supplier;

public interface Observable<T> extends Supplier<T>, BasicEventRegistry<ChangeEvent<T>>{
	T forceGet();
	
	boolean unregister();
	
	boolean isRegistered();
	
	boolean register();
	
	/**
	 * @param exists true表示仅当存在时才注册
	 * @return
	 */
	boolean register(boolean exists);
	
	BasicEventRegistry<ChangeEvent<T>> getRegistry();
	
	EventRegistration registerListener(boolean exists, EventListener<ChangeEvent<T>> eventListener);
}
