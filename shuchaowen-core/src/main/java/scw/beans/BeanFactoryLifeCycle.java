package scw.beans;

import scw.beans.annotation.Bean;
import scw.value.property.PropertyFactory;

/**
 * 此类的实例会在beanFactory状态变化时调用
 * @author shuchaowen
 *
 */
@Bean(proxy=false)
public interface BeanFactoryLifeCycle {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
}
