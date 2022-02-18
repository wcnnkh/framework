package io.basc.framework.jpa.beans;

import javax.persistence.EntityManagerFactory;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.jpa.JpaMethodInterceptor;

public class RepositoryDefinition extends DefaultBeanDefinition {

	public RepositoryDefinition(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.getAop().canProxy(getTargetClass()) && beanFactory.isInstance(EntityManagerFactory.class);
	}

	@Override
	protected Object createInternal(Class<?> targetClass, ParameterDescriptors parameterDescriptors, Object[] params) {
		EntityManagerFactory factory = beanFactory.getInstance(EntityManagerFactory.class);
		return beanFactory.getAop().getProxy(targetClass, null, new JpaMethodInterceptor(factory, targetClass))
				.create(parameterDescriptors.getTypes(), params);
	}
}
