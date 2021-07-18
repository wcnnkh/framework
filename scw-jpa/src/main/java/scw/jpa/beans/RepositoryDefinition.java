package scw.jpa.beans;

import javax.persistence.EntityManagerFactory;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;
import scw.jpa.JpaMethodInterceptor;

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
