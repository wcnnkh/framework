package io.basc.framework.context.ioc;

import java.lang.reflect.Method;

import io.basc.framework.mapper.ParameterDescriptor;

public interface IocResolverExtend {
	default ValueDefinition resolveValueDefinition(ParameterDescriptor parameterDescriptor, IocResolver chain) {
		return chain.resolveValueDefinition(parameterDescriptor);
	}

	default MethodIocDefinition resolveInitDefinition(Method method, IocResolver chain) {
		return chain.resolveInitDefinition(method);
	}

	default MethodIocDefinition resolveDestroyDefinition(Method method, IocResolver chain) {
		return chain.resolveDestroyDefinition(method);
	}

	default AutowiredDefinition resolveAutowiredDefinition(ParameterDescriptor parameterDescriptor, IocResolver chain) {
		return chain.resolveAutowiredDefinition(parameterDescriptor);
	}

}
