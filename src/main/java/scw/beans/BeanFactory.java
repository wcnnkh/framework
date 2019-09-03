package scw.beans;

import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	boolean contains(String name);

	/**
	 * 获取一个bean的定义
	 * 
	 * 如果你不知道此方法的用途请不要随便使用
	 * @param name
	 * @return
	 */
	BeanDefinition getBeanDefinition(String name);
}
