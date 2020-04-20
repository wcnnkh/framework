package scw.beans;

import java.util.Collection;

import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	/**
	 * 获取一个bean的定义
	 * 
	 * 如果你不知道此方法的用途请不要随便使用
	 * 
	 * @param name
	 * @return
	 */
	BeanDefinition getDefinition(String name);
	
	BeanDefinition getDefinition(Class<?> clazz);

	Collection<String> getFilterNames();
}
