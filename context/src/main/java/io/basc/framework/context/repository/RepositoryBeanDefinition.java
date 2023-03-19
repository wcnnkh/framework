package io.basc.framework.context.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.orm.repository.adapter.RepositoryMethodInterceptor;

public class RepositoryBeanDefinition extends FactoryBeanDefinition {

	public RepositoryBeanDefinition(BeanFactory beanFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(RepositoryMethodInterceptor.class);
	}

	@Override
	protected Object createInternal(BeanResolver beanResolver, TypeDescriptor typeDescriptor,
			ParameterDescriptors parameterDescriptors, Object[] params) {
		RepositoryMethodInterceptor interceptor = getBeanFactory().getInstance(RepositoryMethodInterceptor.class);
		return getBeanFactory().getAop().getProxy(typeDescriptor.getType(), null, interceptor)
				.create(parameterDescriptors.getTypes(), params);
	}
}
