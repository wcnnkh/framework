package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanLifecycleManager;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FactoryBean;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.ExecutionException;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BeanDefinitionFactoryBean<T> implements FactoryBean<T> {
	private final String name;
	private final BeanDefinition beanDefinition;
	private final ExecutionParametersExtractor executionParametersExtractor;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws ExecutionException, UnsupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCreated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T getObject() throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}

}
