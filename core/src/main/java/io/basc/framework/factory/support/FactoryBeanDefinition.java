package io.basc.framework.factory.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.ServiceLoaderFactory;

public class FactoryBeanDefinition extends DefaultBeanDefinition {
	private BeanFactory beanFactory;

	public FactoryBeanDefinition(BeanFactory beanFactory, Class<?> type) {
		this(beanFactory, TypeDescriptor.valueOf(type));
	}

	public FactoryBeanDefinition(BeanFactory beanFactory, TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
		this.beanFactory = beanFactory;
	}

	@Override
	public BeanResolver getBeanResolver() {
		BeanResolver beanResolver = super.getBeanResolver();
		return beanFactory == null ? beanFactory.getBeanResolver() : beanResolver;
	}

	@Override
	public ServiceLoaderFactory getServiceLoaderFactory() {
		ServiceLoaderFactory serviceLoaderFactory = super.getServiceLoaderFactory();
		return serviceLoaderFactory == null ? beanFactory : serviceLoaderFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
