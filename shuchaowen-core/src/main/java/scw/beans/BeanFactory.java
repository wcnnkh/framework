package scw.beans;

import scw.aop.AopProxyAdapter;
import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	BeanDefinition getDefinition(String name);

	BeanDefinition getDefinition(Class<?> clazz);

	AopProxyAdapter getAopProxyAdapter();
}
