package io.basc.framework.beans.factory.config;

public interface BeanInjector {
	void inject(Object bean, String beanName);
}
