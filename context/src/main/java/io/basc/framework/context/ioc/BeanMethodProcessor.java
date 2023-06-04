package io.basc.framework.context.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.beans.factory.FactoryException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.mapper.support.ExecutableParameterDescriptors;

public class BeanMethodProcessor extends IocProcessor {
	private final Method method;

	public BeanMethodProcessor(Context context, Method method) {
		super(context);
		this.method = method;
		checkMethod(method);
	}

	@Override
	public void processPostBean(Object bean, BeanDefinition definition) throws FactoryException {
		if (acceptModifiers(definition, bean, method.getModifiers())) {
			processMethod(getContext(), bean, definition, method);
		}
	}

	public Method getMethod() {
		return method;
	}

	public Object processMethod(Context context, Object bean, BeanDefinition definition, Method method) {
		Object instance = Modifier.isStatic(method.getModifiers()) ? null : bean;
		ParameterDescriptors parameterDescriptors = new ExecutableParameterDescriptors(
				definition.getTypeDescriptor().getType(), method);
		Object[] params = context.getBeanResolver().getParameters(parameterDescriptors);
		return ReflectionUtils.invoke(method, instance, params);
	}
}
