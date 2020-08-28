package scw.event.support;

import scw.event.EventListener;
import scw.event.EventRegistration;

public interface DynamicValue<T> {
	T getValue();

	/**
	 * 返回当前是否是动态的
	 * @return
	 */
	boolean isDynamic();

	/**
	 * 切换动态状态
	 * @param dynamic 是否是动态的
	 * @return
	 */
	boolean switchDynamicState(boolean dynamic);

	EventRegistration registerListener(EventListener<ValueEvent<T>> eventListener);
}
