package io.basc.framework.factory.support;

import io.basc.framework.aop.Aop;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanResolver;
import io.basc.framework.convert.TypeDescriptor;

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
		return beanResolver == null ? beanFactory.getBeanResolver() : beanResolver;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Override
	public Aop getAop() {
		Aop aop = super.getAop();
		return aop == null ? beanFactory.getAop() : aop;
	}
}
