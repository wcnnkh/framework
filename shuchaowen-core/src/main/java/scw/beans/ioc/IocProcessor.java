package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public interface IocProcessor {
	void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	/**
	 * 是否是全局的, 为了兼容老版本支持静态方法
	 * @return
	 */
	boolean isGlobal();
}
