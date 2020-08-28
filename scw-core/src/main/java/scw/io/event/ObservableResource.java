package scw.io.event;

import scw.event.EventRegistration;

public abstract class ObservableResource<T> {
	private final T resource;

	public ObservableResource(T resource) {
		this.resource = resource;
	}

	public T getResource() {
		return resource;
	}

	/**
	 * 默认情况下只有当资源存在时才会监听
	 * @param eventListener
	 * @return
	 */
	public EventRegistration registerListener(ObservableResourceEventListener<T> eventListener){
		return registerListener(eventListener, true);
	}

	/**
	 * @param eventListener
	 * @param isExist true表示只有在资源存在时才注册此监听
	 * @return
	 */
	public abstract EventRegistration registerListener(ObservableResourceEventListener<T> eventListener, boolean isExist);
}
