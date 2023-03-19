package io.basc.framework.context;

import java.util.Collection;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.ParameterDescriptor;

public interface ContextResolver extends TypeFilter {
	ProviderDefinition getProviderDefinition(Class<?> clazz);

	boolean hasContext(ParameterDescriptor parameterDescriptor);

	Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz);
}
