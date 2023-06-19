package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionLifecycle;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceRegistry;
import lombok.Data;

@Data
public class DefaultBeanDefinition<T extends Executor> implements BeanDefinition {
	private String resourceDescription;
	// 默认使用单例
	private boolean singleton = true;
	private BeanDefinition originatingBeanDefinition;
	private Elements<? extends T> executors = Elements.empty();
	private Scope scope = Scope.DEFAULT;
	private ServiceRegistry<BeanDefinitionLifecycle> lifecycles = new ServiceRegistry<>();

	@Override
	public void init(Executor constructor, Object bean) throws BeansException {
		for (BeanDefinitionLifecycle lifecycle : lifecycles.getServices()) {
			lifecycle.init(constructor, bean);
		}
	}

	@Override
	public void destroy(Executor constructor, Object bean) throws BeansException {
		for (BeanDefinitionLifecycle lifecycle : lifecycles.getServices().reverse()) {
			lifecycle.destroy(constructor, bean);
		}
	}

	@Override
	public Elements<? extends T> getConstructors() {
		return executors;
	}
}
