package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.util.SpiServiceLoader;

public class BeanFactorySpiServiceLoader<S> extends SpiServiceLoader<S> {
	private final BeanFactory beanFactory;

	public BeanFactorySpiServiceLoader(Class<S> svc, BeanFactory beanFactory) {
		super(svc);
		this.beanFactory = beanFactory;
	}

	@Override
	protected <T> T newInstance(Class<T> serviceClass) throws Throwable {
		return beanFactory.getBean(serviceClass);
	}
}
