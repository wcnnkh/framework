package io.basc.framework.beans.repository;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.orm.repository.adapter.RepositoryMethodInterceptor;

public class RepositoryBeanDefinition extends DefaultBeanDefinition {

	public RepositoryBeanDefinition(ConfigurableBeanFactory beanFactory,
			Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(RepositoryMethodInterceptor.class);
	}

	@Override
	protected Object createProxyInstance(Class<?> targetClass,
			Class<?>[] parameterTypes, Object[] args) {
		RepositoryMethodInterceptor interceptor = beanFactory
				.getInstance(RepositoryMethodInterceptor.class);
		return beanFactory.getAop().getProxy(targetClass, null, interceptor)
				.create(parameterTypes, args);
	}
}
