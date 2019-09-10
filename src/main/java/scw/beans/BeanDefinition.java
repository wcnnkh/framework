package scw.beans;

import scw.core.InstanceDefinition;

public interface BeanDefinition extends InstanceDefinition{
	String getId();

	String[] getNames();

	Class<?> getType();

	boolean isSingleton();
	
	/**
	 * 是否可以使用默认方式实例化
	 * @return
	 */
	boolean isInstance();

	void autowrite(Object bean) throws Exception;

	void init(Object bean) throws Exception;

	void destroy(Object bean) throws Exception;
}
