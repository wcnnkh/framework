package io.basc.framework.context.ioc;

import java.lang.reflect.Method;

import io.basc.framework.mapper.ParameterDescriptor;

public class IocResolverConfiguration implements IocResolver {

	@Override
	public ValueDefinition resolveValueDefinition(ParameterDescriptor parameterDescriptor) {
		return null;
	}

	@Override
	public MethodIocDefinition resolveInitDefinition(Method method) {
		return null;
	}

	@Override
	public MethodIocDefinition resolveDestroyDefinition(Method method) {
		return null;
	}

	@Override
	public AutowiredDefinition resolveAutowiredDefinition(ParameterDescriptor parameterDescriptor) {
		return null;
	}

}
