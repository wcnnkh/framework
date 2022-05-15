package io.basc.framework.beans.repository;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.orm.repository.adapter.RepositoryMethodInterceptor;

public class RepositoryBeanDefinition extends DefaultBeanDefinition {
	private final String repositoryName;

	public RepositoryBeanDefinition(ConfigurableBeanFactory beanFactory,
			Class<?> sourceClass, String repositoryName) {
		super(beanFactory, sourceClass);
		this.repositoryName = repositoryName;
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(repositoryName);
	}

	@Override
	protected Object createProxyInstance(Class<?> targetClass,
			Class<?>[] parameterTypes, Object[] args) {
		Repository repository = beanFactory.getInstance(repositoryName);
		RepositoryMethodInterceptor interceptor = new RepositoryMethodInterceptor(
				repository);
		interceptor.configure(beanFactory);
		return beanFactory.getAop().getProxy(targetClass, null, interceptor)
				.create(parameterTypes, args);
	}
}
