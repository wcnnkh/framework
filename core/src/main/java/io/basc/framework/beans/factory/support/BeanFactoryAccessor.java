package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.BeanFactoryAware;

public class BeanFactoryAccessor implements BeanFactoryAware {
	private transient BeanFactory beanFactory;

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
