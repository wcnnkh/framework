package io.basc.framework.observe;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.EventRegistry;
import io.basc.framework.util.Registration;

/**
 * 推模式
 * 
 * @author shuchaowen
 *
 * @param <E> 事件类型
 */
public interface Push<E> extends EventRegistry<E> {
	/**
	 * 注册一个监听接收推送
	 */
	@Override
	Registration registerListener(EventListener<E> eventListener) throws EventRegistrationException;
}
