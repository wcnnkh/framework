package io.basc.framework.jpa.beans;

import javax.persistence.EntityManagerFactory;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.jpa.JpaMethodInterceptor;
import io.basc.framework.mapper.ParameterDescriptors;

public class RepositoryDefinition extends FactoryBeanDefinition {

	public RepositoryDefinition(BeanFactory beanFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().getAop().canProxy(getTypeDescriptor().getType())
				&& getBeanFactory().isInstance(EntityManagerFactory.class);
	}

	@Override
	protected Object createInternal(BeanResolver beanResolver, TypeDescriptor typeDescriptor,
			ParameterDescriptors parameterDescriptors, Object[] params) {
		EntityManagerFactory factory = getBeanFactory().getInstance(EntityManagerFactory.class);
		return getBeanFactory().getAop()
				.getProxy(getTypeDescriptor().getType(), null,
						new JpaMethodInterceptor(factory, typeDescriptor.getType()))
				.create(parameterDescriptors.getTypes(), params);
	}

}
