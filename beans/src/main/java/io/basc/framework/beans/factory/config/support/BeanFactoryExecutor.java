package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.execution.reflect.ReflectionMethod;

public class BeanFactoryExecutor extends ReflectionMethod {
	private final String targetBeanName;
	private final BeanFactory beanFactory;

	public BeanFactoryExecutor(Method executable, BeanFactory beanFactory,
			String targetBeanName) {
		super(executable);
		this.beanFactory = beanFactory;
		this.targetBeanName = targetBeanName;
	}

	@Override
	public Object getTarget() {
		Object target = super.getTarget();
		if (target == null && !Modifier.isStatic(getMember().getModifiers())) {
			target = beanFactory.getBean(targetBeanName);
		}
		return target;
	}
}
