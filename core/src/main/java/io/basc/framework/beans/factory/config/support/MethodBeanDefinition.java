package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MethodBeanDefinition implements BeanDefinition {
	private final String name;
	private final Scope scope;
	private final boolean singleton;
	private final BeanDefinition originatingBeanDefinition;
	private final Method method;
	private final Class<?> originatingClass;
	private volatile Executor executor;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getResourceDescription() {
		return method.toString();
	}

	@Override
	public <T> void destroy(Executor executor, T bean) throws BeansException {

	}

	public Executor getExecutor() {
		if (executor == null) {
			synchronized (this) {
				if (executor == null) {
					executor = new BeanFactoryExecutor(TypeDescriptor.valueOf(originatingClass), method,
							originatingBeanDefinition.getName());
				}
			}
		}
		return executor;
	}

	@Override
	public Elements<? extends Executor> getExecutors() {
		return Elements.singleton(getExecutor());
	}

	@Override
	public <T> void init(Executor executor, Object bean) throws BeansException {
	}
}
