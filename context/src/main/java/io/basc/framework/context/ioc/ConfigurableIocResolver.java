package io.basc.framework.context.ioc;

import java.lang.reflect.Method;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mapper.ParameterDescriptor;

public class ConfigurableIocResolver extends ConfigurableServices<IocResolverExtend> implements IocResolver {
	private IocResolver defaultResolver;
	
	public ConfigurableIocResolver() {
		super(IocResolverExtend.class);
	}

	public IocResolver getDefaultResolver() {
		return defaultResolver;
	}

	public void setDefaultResolver(IocResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

	@Override
	public ValueDefinition resolveValueDefinition(ParameterDescriptor parameterDescriptor) {
		return new IocResolverChain(iterator(), getDefaultResolver()).resolveValueDefinition(parameterDescriptor);
	}

	@Override
	public MethodIocDefinition resolveInitDefinition(Method method) {
		return new IocResolverChain(iterator(), getDefaultResolver()).resolveInitDefinition(method);
	}

	@Override
	public MethodIocDefinition resolveDestroyDefinition(Method method) {
		return new IocResolverChain(iterator(), getDefaultResolver()).resolveInitDefinition(method);
	}

	@Override
	public AutowiredDefinition resolveAutowiredDefinition(ParameterDescriptor parameterDescriptor) {
		return new IocResolverChain(iterator(), getDefaultResolver()).resolveAutowiredDefinition(parameterDescriptor);
	}

}
