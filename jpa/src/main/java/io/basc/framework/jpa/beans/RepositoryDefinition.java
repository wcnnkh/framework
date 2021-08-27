package io.basc.framework.jpa.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.instance.InstanceException;
import io.basc.framework.jpa.JpaMethodInterceptor;

import javax.persistence.EntityManagerFactory;

public class RepositoryDefinition extends DefaultBeanDefinition {

	public RepositoryDefinition(ConfigurableBeanFactory beanFactory,
			Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(EntityManagerFactory.class);
	}

	@Override
	public Object create() throws InstanceException {
		EntityManagerFactory factory = beanFactory
				.getInstance(EntityManagerFactory.class);
		return new JpaMethodInterceptor(factory, getTargetClass());
	}
}
