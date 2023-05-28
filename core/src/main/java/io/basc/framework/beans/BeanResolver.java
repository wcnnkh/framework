package io.basc.framework.beans;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.DefaultParameterFactory;
import io.basc.framework.factory.ParameterFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ParameterDescriptor;

public interface BeanResolver extends DefaultParameterFactory, ParameterFactory {
	boolean isSingleton(TypeDescriptor typeDescriptor);

	@Nullable
	String getId(TypeDescriptor typeDescriptor);

	Collection<String> getNames(TypeDescriptor typeDescriptor);

	boolean isAopEnable(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor);

	boolean isNullable(ParameterDescriptor parameterDescriptor);

	boolean isExternal(TypeDescriptor typeDescriptor);
}