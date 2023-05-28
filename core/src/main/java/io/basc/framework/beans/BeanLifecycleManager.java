package io.basc.framework.beans;

import io.basc.framework.event.BroadcastEventRegistry;

/**
 * 生命周期管理
 * 
 * @author wcnnkh
 *
 */
public interface BeanLifecycleManager {

	BroadcastEventRegistry<BeanLifecycleEvent> getBeanLifecycleEventRegistry();

	void dependence(String beanName, Object bean) throws BeansException;

	void init(String beanName, Object bean) throws BeansException;

	void destroy(String beanName, Object bean) throws BeansException;
}
