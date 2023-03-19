package io.basc.framework.context;

import java.util.Collection;

import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.ParameterDescriptor;

public interface ContextResolverExtend {
	default ProviderDefinition getProviderDefinition(Class<?> clazz, ContextResolver chain) {
		return chain.getProviderDefinition(clazz);
	}

	default boolean hasContext(ParameterDescriptor parameterDescriptor, ContextResolver chain) {
		return chain.hasContext(parameterDescriptor);
	}

	default Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz, ContextResolver chain) {
		return chain.resolveBeanDefinitions(clazz);
	}
}
