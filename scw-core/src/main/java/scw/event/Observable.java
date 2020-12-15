package scw.event;

public interface Observable<T> extends EventRegistration, BasicEventRegistry<ChangeEvent<T>>{
	T get();
	
	T forceGet();
	
	boolean isRegistered();
	
	boolean register();
	
	/**
	 * @param exists true表示仅当存在时才注册
	 * @return
	 */
	boolean register(boolean exists);
	
	/**
	 * @param exists true表示仅当存在时才注册
	 * @param eventListener
	 * @return
	 */
	EventRegistration registerListener(boolean exists, EventListener<ChangeEvent<T>> eventListener);
}
