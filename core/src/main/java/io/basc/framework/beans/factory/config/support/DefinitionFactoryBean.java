package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.NoSuchConstructorException;
import io.basc.framework.execution.param.Parameters;

public class DefinitionFactoryBean implements FactoryBean<Object> {
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	private final BeanDefinition beanDefinition;
	private volatile Executor executor;
	private volatile Object singleton;

	public DefinitionFactoryBean(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}

	@Override
	public boolean canExecuted() {
		return getExecutor(false) != null;
	}

	public Object create() throws BeansException {
		Executor constructor = getExecutor(true);
		try {
			if (autowireCapableBeanFactory != null) {
				if (autowireCapableBeanFactory.canExtractParameters(constructor)) {
					Parameters parameters = autowireCapableBeanFactory.extractParameters(constructor);
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
						method.getTargetTypeDescriptor().getType());
				return method.execute(target, parameters);
			}
		}
		return executor.execute(parameters);
	}

	@Override
	public Object execute() throws BeansException {
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

	public Executor getExecutor(boolean required) throws NoSuchConstructorException {
		if (executor == null) {
			synchronized (this) {
				if (executor == null) {
					if (autowireCapableBeanFactory != null && beanDefinition.getParameters().isEmpty()) {
						for (Executor constructor : beanDefinition.getConstructors()) {
							if (autowireCapableBeanFactory.canExtractParameters(constructor)) {
								this.executor = constructor;
								break;
							}
						}
					}

					if (executor == null) {
						for (Executor constructor : beanDefinition.getConstructors()) {
							if (constructor.canExecuted(beanDefinition.getParameters())) {
								this.executor = constructor;
								break;
							}
						}
					}
				}
			}
		}

		if (required && executor == null) {
			throw new NoSuchConstructorException("Unable to match to constructor");
		}
		return executor;
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return getExecutor(true).getReturnTypeDescriptor();
	}

	@Override
	public boolean isSingleton() {
		return beanDefinition.isSingleton();
	}

	public void setAutowireCapableBeanFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}
}
