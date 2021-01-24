package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;

public interface IocProcessor {
	void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws Exception;
}
