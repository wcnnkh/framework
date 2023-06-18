package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class DefaultBeanDefinition implements BeanDefinition {
	private final String name;
	private String resourceDescription;
	private boolean singleton = true;
	private BeanDefinition originatingBeanDefinition;
	private Elements<? extends Executor> executors = Elements.empty();
	private Scope scope = Scope.DEFAULT;

	@Override
	public <T> void destroy(Executor executor, T bean) throws BeansException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void init(Executor executor, Object bean) throws BeansException {
		// TODO Auto-generated method stub
	}
}
