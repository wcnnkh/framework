package scw.beans;

import scw.aop.Aop;
import scw.beans.event.BeanEvent;
import scw.core.instance.InstanceFactory;
import scw.event.BasicEventDispatcher;

public interface BeanFactory extends InstanceFactory {
	BeanDefinition getDefinition(String name);

	BeanDefinition getDefinition(Class<?> clazz);

	/**
	 * @param name
	 * @param autoload 如果不存在是否加载
	 * @return
	 */
	BeanDefinition getDefinition(String name, boolean autoload);

	/**
	 * @param clazz
	 * @param autoload 如果不存在是否加载
	 * @return
	 */
	BeanDefinition getDefinition(Class<?> clazz, boolean autoload);

	Aop getAop();

	/**
	 * bean的事件派发
	 * 
	 * @return
	 */
	BasicEventDispatcher<BeanEvent> getEventDispatcher();
	
	/**
	 * @param name
	 * @param autoload 如果不存在是否自动加载
	 * @return
	 */
	boolean isInstance(String name, boolean autoload);
	
	/**
	 * @param clazz
	 * @param autoload 如果不存在是否自动加载
	 * @return
	 */
	boolean isInstance(Class<?> clazz, boolean autoload);
	
	/**
	 * @param name
	 * @param autoload 如果不存在是否自动加载
	 * @return
	 */
	boolean isSingleton(String name, boolean autoload);
	
	/**
	 * @param clazz
	 * @param autoload 如果不存在是否自动加载
	 * @return
	 */
	boolean isSingleton(Class<?> clazz, boolean autoload);
}
