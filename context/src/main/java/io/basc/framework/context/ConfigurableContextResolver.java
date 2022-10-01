package io.basc.framework.context;

import java.util.Collection;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.ConfigurableServices;

public class ConfigurableContextResolver extends ConfigurableServices<ContextResolverExtend>
		implements ContextResolver {
	private ContextResolver defaultResolver;

	public ConfigurableContextResolver() {
		super(ContextResolverExtend.class);
	}

	public ContextResolver getDefaultResolver() {
		return defaultResolver;
	}

	public void setDefaultResolver(ContextResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz) {
		return ContextResolverChain.build(iterator(), getDefaultResolver()).getProviderDefinition(clazz);
	}

	@Override
	public boolean hasContext(ParameterDescriptor parameterDescriptor) {
		return ContextResolverChain.build(iterator(), getDefaultResolver()).hasContext(parameterDescriptor);
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz) {
		return ContextResolverChain.build(iterator(), getDefaultResolver()).resolveBeanDefinitions(clazz);
	}

}
