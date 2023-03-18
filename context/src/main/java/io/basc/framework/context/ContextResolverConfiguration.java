package io.basc.framework.context;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.BeanDefinition;

public class ContextResolverConfiguration implements ContextResolver {
	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz) {
		return null;
	}

	@Override
	public boolean hasContext(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz) {
		return Collections.emptySet();
	}
}
