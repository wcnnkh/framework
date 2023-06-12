package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class ConstructorBeanDefinition implements BeanDefinition {
	private final TypeDescriptor source;
	private final Scope scope;
	private final boolean singleton;

	@Override
	public String getResourceDescription() {
		return source.getName();
	}

	@Override
	public <T> void destroy(Executor executor, T bean) throws BeansException {
		// TODO Auto-generated method stub

	}

	@Override
	public Elements<? extends Executor> getExecutors() {
		
	}

	@Override
	public BeanDefinition getOriginatingBeanDefinition() {
		return null;
	}

	@Override
	public <T> void init(Executor executor, Object bean) throws BeansException {
		// TODO Auto-generated method stub

	}
}
