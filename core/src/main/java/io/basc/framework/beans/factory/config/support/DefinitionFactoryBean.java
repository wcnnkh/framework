package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Constructor;
import io.basc.framework.execution.NoSuchConstructorException;

public class DefinitionFactoryBean implements FactoryBean<Object> {
	private final BeanDefinition beanDefinition;
	private volatile Constructor constructor;

	public DefinitionFactoryBean(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}

	@Override
	public boolean canExecuted() {
		return getConstructor(false) != null;
	}

	@Override
	public Object execute() throws BeansException {
		Constructor constructor = getConstructor(true);
		try {
			return constructor.execute(beanDefinition.getParameters());
		} catch (Throwable e) {
			throw new BeansException(beanDefinition.getName(), e);
		}
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public Constructor getConstructor(boolean required) throws NoSuchConstructorException {
		if (constructor == null) {
			synchronized (this) {
				if (constructor == null) {
					for (Constructor constructor : beanDefinition.getConstructors()) {
						if (constructor.canExecuted(beanDefinition.getParameters())) {
							this.constructor = constructor;
							break;
						}
					}
				}
			}
		}

		if (required && constructor == null) {
			throw new NoSuchConstructorException("Unable to match to constructor");
		}
		return constructor;
	}

	public Constructor getConstructor() {
		return constructor;
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return getConstructor(true).getReturnTypeDescriptor();
	}

	@Override
	public boolean isSingleton() {
		return beanDefinition.isSingleton();
	}
}
