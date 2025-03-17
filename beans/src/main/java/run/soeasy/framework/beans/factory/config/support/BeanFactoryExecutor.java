package run.soeasy.framework.beans.factory.config.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.execution.reflect.ReflectionMethod;

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
