package io.basc.framework.beans;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;

public interface FactoryBeanResolver {
	boolean isAopEnable(TypeDescriptor type);

	boolean isInit(Class<?> sourceClass, Method method);

	boolean isDestory(Class<?> sourceClass, Method method);

	@Nullable
	AutowiredDefinition getAutowiredDefinition(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isService(TypeDescriptor typeDescriptor);

	String getName(TypeDescriptor type);

	String[] getAliasNames(TypeDescriptor type);
}
