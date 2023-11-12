package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;

public class BeanFactoryExecutor extends ReflectionMethodExecutor implements Executor {
	private final String targetBeanName;
	private final BeanFactory beanFactory;

	public BeanFactoryExecutor(Method executable, TypeDescriptor source, BeanFactory beanFactory,
			String targetBeanName) {
		super(executable, source);
		this.beanFactory = beanFactory;
		this.targetBeanName = targetBeanName;
	}

	@Override
	public Object getTarget() {
		Object target = super.getTarget();
		if (target == null && !Modifier.isStatic(getExecutable().getModifiers())) {
			target = beanFactory.getBean(targetBeanName);
		}
		return target;
	}
}
