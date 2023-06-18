package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.Getter;

@Data
public class ClassBeanDefinition implements BeanDefinition {
	private final String name;
	private final Class<?> sourceClass;
	private final Scope scope;
	private final boolean singleton;
	private BeanDefinition originatingBeanDefinition;

	@Override
	public String getResourceDescription() {
		return sourceClass.toString();
	}

	@Override
	public <T> void destroy(Executor executor, T bean) throws BeansException {
	}

	@Override
	public <T> void init(Executor executor, Object bean) throws BeansException {
	}

	@Override
	public Elements<? extends Executor> getExecutors() {
		
	}
}
