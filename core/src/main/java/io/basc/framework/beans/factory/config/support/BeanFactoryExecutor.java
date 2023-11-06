package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.support.DefaultParameterDescriptor;
import io.basc.framework.util.ArrayUtils;

public class BeanFactoryExecutor extends ReflectionMethod implements Executor {
	private static final ParameterDescriptor PARAMETER_DESCRIPTOR = new DefaultParameterDescriptor("beanFactory",
			TypeDescriptor.valueOf(BeanFactory.class));
	private final String targetBeanName;

	public BeanFactoryExecutor(Method executable, TypeDescriptor source, String targetBeanName) {
		super(executable, source);
		this.targetBeanName = targetBeanName;
	}

	@Override
	public ParameterDescriptor[] getParameterDescriptors() {
		return ArrayUtils.merge(new ParameterDescriptor[] { PARAMETER_DESCRIPTOR }, super.getParameterDescriptors());
	}

	@Override
	public Object execute(Object[] args) throws Throwable {
		BeanFactory beanFactory = (BeanFactory) args[0];
		Object[] params = Arrays.copyOfRange(args, 1, args.length);
		Object target = beanFactory.getBean(targetBeanName);
		return ReflectionUtils.invoke(getExecutable(), target, params);
	}

}
