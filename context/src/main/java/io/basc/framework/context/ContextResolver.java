package io.basc.framework.context;

import java.util.Collection;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.BeanDefinition;

public interface ContextResolver {
	ProviderDefinition getProviderDefinition(Class<?> clazz);

	boolean hasContext(ParameterDescriptor parameterDescriptor);

	Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz);
}
