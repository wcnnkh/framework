package scw.event;

public interface Observable<T> extends BasicEventRegistry<ChangeEvent<T>>{
	T get();
	
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
