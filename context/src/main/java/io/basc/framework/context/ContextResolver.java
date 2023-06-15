package io.basc.framework.context;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ParameterDescriptor;

public interface ContextResolver extends TypeFilter {
	boolean hasContext(ParameterDescriptor parameterDescriptor);

	@Nullable
	BeanDefinition resolveBeanDefinition(Class<?> sourceClass);

	@Nullable
	BeanDefinition resolveBeanDefinition(Class<?> sourceClass, BeanDefinition originBeanDefinition, Method method);
}
