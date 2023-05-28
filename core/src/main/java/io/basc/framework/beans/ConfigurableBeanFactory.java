package io.basc.framework.beans;

import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.util.alias.AliasRegistry;

public interface ConfigurableBeanFactory extends BeanFactory, AliasRegistry {
	/**
	 * bean的生命周期事件注册器
	 * 
	 * @return
	 */
	BroadcastEventRegistry<BeanLifecycleEvent> getBeanLifecycleEventRegistry();

	/**
	 * 初始化容器
	 * 
	 * @throws BeansException
	 */
	void init() throws BeansException;

	/**
	 * 销毁容器
	 * 
	 * @throws BeansException
	 */
	void destory() throws BeansException;

	/**
	 * 销毁一个bean
	 * 
	 * @param beanName
	 * @param bean
	 * @throws FactoryException
	 */
	void destroyBean(String beanName, Object bean) throws FactoryException;

	/**
	 * 注册一个bean
	 * 
	 * @param factoryBean
	 */
	void registerBean(FactoryBean<Object> factoryBean);

	/**
	 * 删除一个bean
	 * 
	 * @param beanName
	 */
	void removeBean(String beanName);

}
