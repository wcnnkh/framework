package scw.beans;

import java.util.Collection;

import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	/**
	 * 表示是否受BeanFactory管理,并不代表可以使用getInstance()获取到实例
	 * 
	 * @param name
	 * @return
	 */
	boolean contains(String name);

	/**
	 * 获取一个bean的定义
	 * 
	 * 如果你不知道此方法的用途请不要随便使用
	 * 
	 * @param name
	 * @return
	 */
	BeanDefinition getBeanDefinition(String name);

	Collection<String> getFilterNames();
}
