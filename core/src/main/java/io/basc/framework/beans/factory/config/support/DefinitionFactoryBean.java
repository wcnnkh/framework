package io.basc.framework.beans.factory.config.support;

import java.util.NoSuchElementException;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.Parameters;

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
				if (autowireCapableBeanFactory.canExtractExecutionParameters(constructor)) {
					Parameters parameters = autowireCapableBeanFactory.extractExecutionParameters(constructor);
					return create(constructor, parameters);
				}
			}

			return create(constructor, beanDefinition.getParameters());
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
	public Object getObject() throws BeansException {
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
					if (autowireCapableBeanFactory != null && beanDefinition.getParameters().isEmpty()) {
						for (Function constructor : beanDefinition.getServices()) {
							if (autowireCapableBeanFactory.canExtractExecutionParameters(constructor)) {
								this.function = constructor;
								break;
							}
						}
					}

					if (function == null) {
						for (Function constructor : beanDefinition.getServices()) {
							if (constructor.canExecuted(beanDefinition.getParameters())) {
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
