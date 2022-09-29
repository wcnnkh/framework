package io.basc.framework.factory;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;

public interface BeanResolver extends DefaultParameterFactory, ParameterFactory {
	boolean isSingleton(TypeDescriptor typeDescriptor);

	String getId(TypeDescriptor typeDescriptor);

	Collection<String> getNames(TypeDescriptor typeDescriptor);

	boolean isAopEnable(TypeDescriptor typeDescriptor);
	
	Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor);

	Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor);
	
	boolean isNullable(ParameterDescriptor parameterDescriptor);
	
	boolean isExternal(TypeDescriptor typeDescriptor);
}