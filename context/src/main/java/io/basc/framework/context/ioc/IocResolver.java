package io.basc.framework.context.ioc;

import java.lang.reflect.Method;

import io.basc.framework.core.parameter.ParameterDescriptor;

public interface IocResolver {
	ValueDefinition resolveValueDefinition(ParameterDescriptor parameterDescriptor);

	MethodIocDefinition resolveInitDefinition(Method method);

	MethodIocDefinition resolveDestroyDefinition(Method method);

	AutowiredDefinition resolveAutowiredDefinition(ParameterDescriptor parameterDescriptor);
}
