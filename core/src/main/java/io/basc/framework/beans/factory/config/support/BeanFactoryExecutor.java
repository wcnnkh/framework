package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.support.DefaultParameterDescriptor;
import io.basc.framework.util.element.Elements;

public class BeanFactoryExecutor extends ReflectionMethod implements Executor {
	private static final ParameterDescriptor PARAMETER_DESCRIPTOR = new DefaultParameterDescriptor("beanFactory",
			TypeDescriptor.valueOf(BeanFactory.class));
	private final String targetBeanName;

	public BeanFactoryExecutor(Method executable, TypeDescriptor source, String targetBeanName) {
		super(executable, source);
		this.targetBeanName = targetBeanName;
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return Elements.singleton(PARAMETER_DESCRIPTOR).concat(super.getParameterDescriptors());
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		BeanFactory beanFactory = (BeanFactory) args.first();
		Object[] params = args.skip(1).toArray();
		Object target = beanFactory.getBean(targetBeanName);
		return ReflectionUtils.invoke(getExecutable(), target, params);
	}

}
