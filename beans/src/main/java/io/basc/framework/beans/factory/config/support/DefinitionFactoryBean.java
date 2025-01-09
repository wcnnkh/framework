package io.basc.framework.beans.factory.config.support;

import java.util.NoSuchElementException;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Method;
import io.basc.framework.core.execution.Parameters;

public class DefinitionFactoryBean implements FactoryBean<Object> {
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final BeanDefinition beanDefinition;
	private volatile Function function;
	private volatile Object singleton;

	public DefinitionFactoryBean(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}

	public Object create() throws BeansException {
		Function constructor = getConstructor(true);
		try {
			if (autowireCapableBeanFactory != null) {
				if (autowireCapableBeanFactory.hasParameters(constructor)) {
					Parameters parameters = autowireCapableBeanFactory.getParameters(constructor);
					return create(constructor, parameters);
				}
			}

			return create(constructor, beanDefinition.getExecutionStrategy().getDefaultParameters());
		} catch (Throwable e) {
			throw new BeansException(beanDefinition.getName(), e);
		}
	}

	private Object create(Executor executor, Parameters parameters) throws Throwable {
		if (executor instanceof Method) {
			Method method = (Method) executor;
			BeanDefinition originBeanDefinition = beanDefinition.getOriginatingBeanDefinition();
			if (originBeanDefinition != null) {
				Object target = autowireCapableBeanFactory.getBean(originBeanDefinition.getName(),
						method.getDeclaringTypeDescriptor().getType());
				return method.execute(target, parameters);
			}
		}
		return executor.execute(parameters);
	}

	@Override
	public Object get() throws BeansException {
		if (isSingleton()) {
			if (singleton == null) {
				synchronized (this) {
					if (singleton == null) {
						singleton = create();
					}
				}
			}
			return singleton;
		} else {
			return create();
		}
	}

	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
		return autowireCapableBeanFactory;
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public Function getConstructor(boolean required) {
		if (function == null) {
			synchronized (this) {
				if (function == null) {
					if (autowireCapableBeanFactory != null
							&& beanDefinition.getExecutionStrategy().getDefaultParameters().isEmpty()) {
						for (Function constructor : beanDefinition.getExecutionStrategy()) {
							if (autowireCapableBeanFactory.hasParameters(constructor)) {
								this.function = constructor;
								break;
							}
						}
					}

					if (function == null) {
						for (Function constructor : beanDefinition.getExecutionStrategy()) {
							if (constructor.canExecuted(beanDefinition.getExecutionStrategy().getDefaultParameters())) {
								this.function = constructor;
								break;
							}
						}
					}
				}
			}
		}

		if (required && function == null) {
			throw new NoSuchElementException("Unable to match to constructor");
		}
		return function;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return getConstructor(true).getReturnTypeDescriptor();
	}

	@Override
	public boolean isSingleton() {
		return beanDefinition.isSingleton();
	}

	public void setAutowireCapableBeanFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}
}
