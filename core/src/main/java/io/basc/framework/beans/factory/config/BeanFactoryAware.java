package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.BeanFactory;

public interface BeanFactoryAware {
	void setBeanFactory(BeanFactory beanFactory);
}