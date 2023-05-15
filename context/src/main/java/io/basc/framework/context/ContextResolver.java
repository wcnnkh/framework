package io.basc.framework.context;

import java.lang.reflect.Executable;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ParameterDescriptor;

public interface ContextResolver extends TypeFilter {
	@Nullable
	ProviderDefinition getProviderDefinition(Class<?> clazz);

	boolean hasContext(ParameterDescriptor parameterDescriptor);

	@Nullable
	BeanDefinition resolveBeanDefinition(Class<?> sourceClass);

	/**
	 * 是否可以解析类的executable
	 * 
	 * @param sourceClass 来源类
	 * @return yes/no
	 * @see #resolveBeanDefinition(Class, Executable)
	 */
	boolean canResolveExecutable(Class<?> sourceClass);

	@Nullable
	BeanDefinition resolveBeanDefinition(Class<?> sourceClass, Executable executable);
}
