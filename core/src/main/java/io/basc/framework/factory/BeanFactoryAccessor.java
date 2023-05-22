package io.basc.framework.factory;

import io.basc.framework.beans.BeanFactory;

public class BeanFactoryAccessor implements BeanFactoryAware {
	private transient BeanFactory beanFactory;

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
